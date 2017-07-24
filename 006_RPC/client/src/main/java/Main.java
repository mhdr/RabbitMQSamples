import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class Main {


    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {


        int count = 0;

        while (count < 100) {
            String time = getTime();
            System.out.println(time);

            Thread.sleep(1000);
            count++;
        }

    }

    private static String getTime() throws IOException, InterruptedException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String requestQueueName = "rpc01";
        String replyQueueName = channel.queueDeclare().getQueue();

        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        String message = "Mahmood";

        // use to block until there is a response available
        BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        channel.basicPublish("", requestQueueName, properties, message.getBytes("UTF-8"));

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                if (properties.getCorrelationId().equals(corrId)) {
                    // save in the response queue
                    // so the queue can release the blockage
                    response.offer(new String(body, "UTF-8"));
                }
            }
        };

        channel.basicConsume(replyQueueName, true, consumer);

        // block until there is a response available
        String result=response.take();

        channel.close();
        connection.close();

        return result;
    }

}

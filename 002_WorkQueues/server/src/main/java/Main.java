import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        String queueName = "queue2";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message=new String(body,"UTF-8");

                try {
                    doWork(message);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finally {
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };

        channel.basicConsume(queueName,false,consumer);
    }

    public static void doWork(String message) throws InterruptedException {
        System.out.println(" [x] Received '" + message + "'");

        int sleepTime=new Random().nextInt(2000);

        Thread.sleep(sleepTime);
    }
}

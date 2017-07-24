import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Main {

    private static String requestQueueName = "rpc01";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(requestQueueName, false, false, false, null);
        channel.basicQos(1);

        System.out.println(" [x] Awaiting RPC requests");

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                String responseQueueName = properties.getReplyTo();
                String correlationId = properties.getCorrelationId();

                System.out.println(String.format("CorrelationId: %s, Response Queue: %s", correlationId, responseQueueName));

                String message = new String(body, "UTF-8");
                String response = String.format("Hello %s, Time is : %s", message, new Date().toString());


                AMQP.BasicProperties responseProperties = new AMQP.BasicProperties().builder()
                        .correlationId(correlationId)
                        .build();

                channel.basicPublish("", responseQueueName, responseProperties, response.getBytes("UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(requestQueueName, false, consumer);
    }

}

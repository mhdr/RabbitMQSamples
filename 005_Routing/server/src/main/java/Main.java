import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Queue1();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        thread1.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Queue1();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        thread2.start();
    }


    /**
     * get all routine keys
     */
    public static void Queue1() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "ex01";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchangeName, "warning");
        channel.queueBind(queueName, exchangeName, "info");
        channel.queueBind(queueName, exchangeName, "error");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

            }
        };

        channel.basicConsume(queueName, true, consumer);
    }

    /**
     * get routine key : error
     */
    public static void Queue2() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "ex01";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchangeName, "error");

        System.out.println(" [***] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                System.out.println(" [xxx] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}

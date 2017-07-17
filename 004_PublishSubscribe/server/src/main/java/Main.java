import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    showLog();
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
                    countLong();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread2.start();
    }

    public static void showLog() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "logs";

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                String output = String.format(" [*] : %s", message);
                System.out.println(output);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }

    public static void countLong() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "logs";

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        System.out.println(" [+] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                Statics.countLog++;

                String output = String.format(" [+] : %d", Statics.countLog);
                System.out.println(output);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}

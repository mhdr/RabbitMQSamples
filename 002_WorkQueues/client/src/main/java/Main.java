import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        String queueName = "queue2";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, true, false, false, null);

        int count = 0;

        while (count < 100) {

            Date date = new Date();

            String message = String.format("%d : %s", count, date.toString());

            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes("UTF-8"));
            System.out.println(String.format(" [x] Sent %s", message));

            Thread.sleep(1000);
            count++;
        }
    }
}

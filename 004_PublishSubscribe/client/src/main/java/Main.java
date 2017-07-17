import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "logs";

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT);

        int count = 0;

        while (count < 100) {

            String message = new Date().toString();

            String output = String.format(" [#] Sending Log : %s", message);
            System.out.println(output);

            channel.basicPublish(exchangeName, "", null, message.getBytes());

            Thread.sleep(1000);
            count++;
        }

        channel.close();
        connection.close();
    }
}

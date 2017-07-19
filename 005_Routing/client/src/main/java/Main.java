import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "ex01";

        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        int count=0;

        while (count<1000)
        {

            String message=new Date().toString();
            String routingKey=getNextSeverity();
            channel.basicPublish(exchangeName,routingKey,null,message.getBytes("UTF-8"));

            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

            Thread.sleep(1000);
            count ++;
        }

        channel.close();
        connection.close();

    }

    public static String getNextSeverity() {

        int rand = new Random().nextInt(3);

        if (rand == 0) {
            return "warning";
        } else if (rand == 1) {
            return "info";
        } else if (rand == 2) {
            return "error";
        }

        return "error";
    }
}

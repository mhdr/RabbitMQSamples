import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.swing.event.ChangeEvent;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        String queueName="Hello";

        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection= factory.newConnection();
        Channel channel= connection.createChannel();

        channel.queueDeclare(queueName,false,false,false,null);


        int count=0;

        while (count<100){
            Date date=new Date();
            String message=String.format("Hello World : %s", date.toString());

            channel.basicPublish("",queueName,null,message.getBytes("UTF-8"));

            System.out.println(" [x] Sent '" + message + "'");
            count++;

            Thread.sleep(1000);
        }

        channel.close();
        connection.close();
    }
}

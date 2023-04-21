import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

import org.apache.hadoop.hbase.TableName;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import org.apache.hadoop.hbase.util.Bytes;

public class TablePartF{

    public static void main(String[] args) throws IOException {

        // TODO
        // DON' CHANGE THE 'System.out.println(xxx)' OUTPUT PART
        // OR YOU WON'T RECEIVE POINTS FROM THE GRADER


        // hbase configuration
        Configuration config = HBaseConfiguration.create();

        // get htable
        HTable powerTable = new HTable(config, "powers");

        // get scan
        Scan scan = new Scan();

        // scan requirements
        scan.addFamily(Bytes.toBytes("personal"));
        scan.addFamily(Bytes.toBytes("professional"));
        scan.addFamily(Bytes.toBytes("custom"));

        ResultScanner scanner1 = powerTable.getScanner(scan);

//        int count = 0;

        //outer loop
        for (Result result1 = scanner1.next(); result1 != null; result1 = scanner1.next()){
            String name1 = Bytes.toString(result1.getValue(Bytes.toBytes("professional"), Bytes.toBytes("name")));
            String power1 = Bytes.toString(result1.getValue(Bytes.toBytes("personal"), Bytes.toBytes("power")));
            String color1 = Bytes.toString(result1.getValue(Bytes.toBytes("custom"), Bytes.toBytes("color")));

            //inner loop
            ResultScanner scanner2 = powerTable.getScanner(scan);

            for (Result result2 = scanner2.next(); result2 != null; result2 = scanner2.next()){
                String name2 = Bytes.toString(result2.getValue(Bytes.toBytes("professional"), Bytes.toBytes("name")));
                String power2 = Bytes.toString(result2.getValue(Bytes.toBytes("personal"), Bytes.toBytes("power")));
                String color2 = Bytes.toString(result2.getValue(Bytes.toBytes("custom"), Bytes.toBytes("color")));

                if(color1.equals(color2) && !name1.equals(name2)){
//                    ++count;
                    System.out.println(name1 + ", " + power1 + ", " + name2 + ", " + power2 + ", "+color1);
                }
            }

            scanner2.close();
        }

        scanner1.close();
//        System.out.println(count + " lines of count printed!");

    }
}

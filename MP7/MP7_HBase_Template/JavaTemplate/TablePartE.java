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

public class TablePartE{

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

        ResultScanner scanner = powerTable.getScanner(scan);
        for (Result result = scanner.next(); result != null; result = scanner.next()){
            System.out.println(result);
        }

        scanner.close();
    }
}


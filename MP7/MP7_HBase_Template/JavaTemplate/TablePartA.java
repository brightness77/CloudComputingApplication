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

public class TablePartA{

    public static void main(String[] args) throws IOException {

        // TODO

        // hbase configuration
        Configuration config = HBaseConfiguration.create();

        // hbase admin
        HBaseAdmin admin = new HBaseAdmin(config);

        // table descriptor
        HTableDescriptor table1Descriptor = new HTableDescriptor(TableName.valueOf("powers"));
        HTableDescriptor table2Descriptor = new HTableDescriptor(TableName.valueOf("food"));

        //adding column families
        table1Descriptor.addFamily(new HColumnDescriptor("personal"));
        table1Descriptor.addFamily(new HColumnDescriptor("professional"));
        table1Descriptor.addFamily(new HColumnDescriptor("custom"));

        table2Descriptor.addFamily(new HColumnDescriptor("nutrition"));
        table2Descriptor.addFamily(new HColumnDescriptor("taste"));

        //execute
        admin.createTable(table1Descriptor);
        admin.createTable(table2Descriptor);


    }
}


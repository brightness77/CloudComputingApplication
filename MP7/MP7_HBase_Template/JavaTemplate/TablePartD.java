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
import org.apache.hadoop.hbase.client.Get;


import org.apache.hadoop.hbase.util.Bytes;

public class TablePartD{

    public static void main(String[] args) throws IOException {

        // TODO
        // DON' CHANGE THE 'System.out.println(xxx)' OUTPUT PART
        // OR YOU WON'T RECEIVE POINTS FROM THE GRADER

        // hbase configuration
        Configuration config = HBaseConfiguration.create();

        // get htable
        HTable powerTable = new HTable(config, "powers");


        // get row one
        Result result = powerTable.get(new Get(Bytes.toBytes("row1")));

        String hero = Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("hero")));
        String power = Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("power")));
        String name = Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("name")));
        String xp = Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("xp")));
        String color = Bytes.toString(result.getValue(Bytes.toBytes("custom"), Bytes.toBytes("color")));
        System.out.println("hero: "+hero+", power: "+power+", name: "+name+", xp: "+xp+", color: "+color);

        //get row 19
        result = powerTable.get(new Get(Bytes.toBytes("row19")));

        hero = Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("hero")));
        color = Bytes.toString(result.getValue(Bytes.toBytes("custom"), Bytes.toBytes("color")));
        System.out.println("hero: "+hero+", color: "+color);

        //row 1 again
        result = powerTable.get(new Get(Bytes.toBytes("row1")));

        hero = Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("hero")));
        name = Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("name")));
        color = Bytes.toString(result.getValue(Bytes.toBytes("custom"), Bytes.toBytes("color")));
        System.out.println("hero: "+hero+", name: "+name+", color: "+color);
    }
}


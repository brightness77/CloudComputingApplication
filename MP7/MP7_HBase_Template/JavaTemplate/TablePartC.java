import java.io.IOException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

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

public class TablePartC{

    public static void main(String[] args) throws IOException {

        //TODO

        // hbase configuration
        Configuration config = HBaseConfiguration.create();

        // get htable
        HTable powerTable = new HTable(config, "powers");

        // read csv
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.csv"))) {
            String curLine = bufferedReader.readLine();

            while(curLine != null){
                //seperate line
                String[] curRow = curLine.split(",");

                //put class
                Put put = new Put(Bytes.toBytes(curRow[0]));

                //add parameters
                put.add(Bytes.toBytes("personal"), Bytes.toBytes("hero"), Bytes.toBytes(curRow[1]));
                put.add(Bytes.toBytes("personal"), Bytes.toBytes("power"), Bytes.toBytes(curRow[2]));

                put.add(Bytes.toBytes("professional"), Bytes.toBytes("name"), Bytes.toBytes(curRow[3]));
                put.add(Bytes.toBytes("professional"), Bytes.toBytes("xp"), Bytes.toBytes(curRow[4]));

                put.add(Bytes.toBytes("custom"), Bytes.toBytes("color"), Bytes.toBytes(curRow[5]));

                //save to table
                powerTable.put(put);

                curLine = bufferedReader.readLine();
            }
        }

        System.out.println("Successfully inserted data");
        powerTable.close();

    }
}


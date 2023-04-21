import scala.Tuple2;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.types.StringType;
import org.apache.spark.sql.types.IntegerType;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
//import java.util.function.Function;

public final class MP8_PartE {

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("MP8")
                .getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
        SQLContext sqlContext = new SQLContext(sc);


        /*
         * 1. Setup (10 points): Download the gbook file and write a function
         * to load it in an RDD & DataFrame
         */

        // RDD API
        // Columns: 0: place (string), 1: count1 (int), 2: count2 (int), 3: count3 (int)

        StructType schema = new StructType()
                .add("word", DataTypes.StringType, true)
                .add("count1", DataTypes.IntegerType, true)
                .add("count2", DataTypes.StringType, true)
                .add("count3", DataTypes.StringType, true);

        // Spark SQL - DataSet API

        Dataset<Row> df = spark.read()
                .option("delimiter", "\t")
                .schema(schema)
                .csv("gbooks");

        /*
         * 5. Joining (10 points): The following program construct a new dataframe out of
         * 'df' with a much smaller size, which will allow us to perform a JOIN operation.
         * Do a self-join on 'df2'in lines with the same 'count1' values and see how many
         * lines this JOIN could produce. Answer this question via DataFrame API and Spark SQL API
         */

//        df = df.limit(1000);

        Dataset<Row> df2 = df.select("word", "count1").distinct().limit(100);
//        df2 = df.select("word", "count1").limit(100);


        df2.createOrReplaceTempView("gbooks2");

        // Spark SQL API

//        Dataset<Row> query = spark.sql("SELECT * " +
//                "FROM gbooks2 g1 " +
//                "CROSS JOIN gbooks2 g2 " +
//                "ON g1.count1 = g2.count1 ");

        Dataset<Row> query = spark.sql("SELECT * " +
                "FROM gbooks2 g1 " +
                "INNER JOIN gbooks2 g2 " +
                "ON g1.count1 = g2.count1 ");

//        Dataset<Row> query = spark.sql("SELECT * " +
//                "FROM gbooks2 g1, gbooks2 g2 " +
//                "WHERE g1.count1 = g2.count1 ");

//        Dataset<Row> query = df2.join(df2, df2.col("count1").equalTo(df2.col("count1")));

        System.out.println(query.count());


        // Finish up
        spark.stop();
        sc.stop();
    }
}

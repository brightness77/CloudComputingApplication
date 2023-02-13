import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.lang.Integer;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class TopPopularLinks extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TopPopularLinks(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        //TODO

        Configuration conf = this.getConf();
        FileSystem fs = FileSystem.get(conf);
        Path tmpPath = new Path("./tmp");
        fs.delete(tmpPath, true);

        Job jobA = Job.getInstance(conf, "Link Counts");
        jobA.setOutputKeyClass(IntWritable.class);
        jobA.setOutputValueClass(IntWritable.class);

        jobA.setMapperClass(LinkCountMap.class);
        jobA.setReducerClass(LinkCountReduce.class);

        FileInputFormat.setInputPaths(jobA, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobA, tmpPath);

        jobA.setJarByClass(TopPopularLinks.class);
        jobA.waitForCompletion(true);

        Job jobB = Job.getInstance(conf, "Top Popular Links");
        jobB.setOutputKeyClass(IntWritable.class);
        jobB.setOutputValueClass(IntWritable.class);

        jobB.setMapOutputKeyClass(NullWritable.class);
        jobB.setMapOutputValueClass(IntArrayWritable.class);

        jobB.setMapperClass(TopLinksMap.class);
        jobB.setReducerClass(TopLinksReduce.class);
        jobB.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(jobB, tmpPath);
        FileOutputFormat.setOutputPath(jobB, new Path(args[1]));

        jobB.setInputFormatClass(KeyValueTextInputFormat.class);
        jobB.setOutputFormatClass(TextOutputFormat.class);

        jobB.setJarByClass(TopPopularLinks.class);
        return jobB.waitForCompletion(true) ? 0 : 1;

    }

    public static class IntArrayWritable extends ArrayWritable {
        public IntArrayWritable() {
            super(IntWritable.class);
        }

        public IntArrayWritable(Integer[] numbers) {
            super(IntWritable.class);
            IntWritable[] ints = new IntWritable[numbers.length];
            for (int i = 0; i < numbers.length; i++) {
                ints[i] = new IntWritable(numbers[i]);
            }
            set(ints);
        }
    }

    public static class LinkCountMap extends Mapper<Object, Text, IntWritable, IntWritable> {
        //TODO

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            //TODO

            String line = value.toString();
            int indexOfLinks = line.indexOf(":");

            Integer curWeb = Integer.parseInt(line.substring(0, indexOfLinks));
            String[] curLinks = line.substring(indexOfLinks + 2).split(" ");

            // emit intermediate values
            IntWritable one = new IntWritable(1);
            IntWritable link = new IntWritable(0);
            for(String curLink: curLinks) {
                Integer curParsedLink = Integer.parseInt(curLink);
                if (curLink.equals(curParsedLink)) {
                    //exclude self linking condition
                    continue;
                }
                link.set(curParsedLink);
                context.write(link, one);
            }

            // write self
            IntWritable zero = new IntWritable(0);
            IntWritable source = new IntWritable(curWeb);
            context.write(source, zero);

            //context.write(<IntWritable>, <IntWritable>); // pass this output to reducer
        }
    }

    public static class LinkCountReduce extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        //TODO

        @Override
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //TODO

            Integer totalLinkCount = 0;
            for(IntWritable count: values){
                totalLinkCount += count.get();
            }

            IntWritable countWritable = new IntWritable(totalLinkCount);
            context.write(key, countWritable);

            //context.write(<IntWritable>, <NullWritable>); // print as final output
        }
    }

    public static class TopLinksMap extends Mapper<Text, Text, NullWritable, IntArrayWritable> {

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
        }

        //TODO

        private TreeSet<Pair<Integer, Integer>> countToLinkMap = new TreeSet<>();


        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
            //TODO

            Integer count = Integer.parseInt(value.toString());
            Integer webId = Integer.parseInt(key.toString());

            countToLinkMap.add(new Pair<Integer, Integer>(count, webId));

            if(countToLinkMap.size() > 10){
                countToLinkMap.remove(countToLinkMap.first());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO

            for (Pair<Integer, Integer> item: countToLinkMap) {
                Integer[] integers = {item.second, item.first};
                IntArrayWritable writable = new IntArrayWritable(integers);
                context.write(NullWritable.get(), writable);
            }

            //Cleanup operation starts after all mappers are finished
            //context.write(<NullWritable>, <TextArrayWritable>); // pass this output to reducer
        }
    }


    public static class TopLinksReduce extends Reducer<NullWritable, IntArrayWritable, IntWritable, IntWritable> {

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
        }
        //TODO

        private TreeSet<Pair<Integer, Integer>> countToLinkMap = new TreeSet<>();

        @Override
        public void reduce(NullWritable key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
            //TODO

            for(IntArrayWritable intPair: values){
                Integer[] pair = (Integer[]) intPair.toArray();
                Integer webId = pair[0];
                Integer curCount = pair[1];

                countToLinkMap.add(new Pair<Integer, Integer>(curCount, webId));

                if(countToLinkMap.size() > 10){
                    countToLinkMap.remove(countToLinkMap.first());
                }
            }

            for(Pair<Integer, Integer> pair : countToLinkMap){
                IntWritable webId = new IntWritable(pair.second);
                IntWritable intVal = new IntWritable(pair.first);
                context.write(webId, intVal);
            }

            //context.write(<Text>, <IntWritable>); // print as final output
        }

    }
}


class Pair<A extends Comparable<? super A>,
        B extends Comparable<? super B>>
        implements Comparable<Pair<A, B>> {

    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A extends Comparable<? super A>,
            B extends Comparable<? super B>>
    Pair<A, B> of(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    @Override
    public int compareTo(Pair<A, B> o) {
        int cmp = o == null ? 1 : (this.first).compareTo(o.first);
        return cmp == 0 ? (this.second).compareTo(o.second) : cmp;
    }

    @Override
    public int hashCode() {
        return 31 * hashcode(first) + hashcode(second);
    }

    private static int hashcode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair))
            return false;
        if (this == obj)
            return true;
        return equal(first, ((Pair<?, ?>) obj).first)
                && equal(second, ((Pair<?, ?>) obj).second);
    }

    private boolean equal(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }
}

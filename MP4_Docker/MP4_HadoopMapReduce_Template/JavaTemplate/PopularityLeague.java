import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class PopularityLeague extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new PopularityLeague(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        //TODO

        // Job a is to get all popularity count
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

        jobA.setJarByClass(PopularityLeague.class);
        jobA.waitForCompletion(true);

        // Job b is to get league and rank them
        Job jobB = Job.getInstance(conf, "Popularity League");
        jobB.setOutputKeyClass(IntWritable.class);
        jobB.setOutputValueClass(IntWritable.class);

        jobB.setMapOutputKeyClass(NullWritable.class);
        jobB.setMapOutputValueClass(IntArrayWritable.class);

        jobB.setMapperClass(PopularityLeagueMap.class);
        jobB.setReducerClass(PopularityLeagueReduce.class);
        jobB.setNumReduceTasks(1);

        FileInputFormat.setInputPaths(jobB, tmpPath);
        FileOutputFormat.setOutputPath(jobB, new Path(args[1]));

        jobB.setInputFormatClass(KeyValueTextInputFormat.class);
        jobB.setOutputFormatClass(TextOutputFormat.class);

        jobB.setJarByClass(PopularityLeague.class);
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

    public static String readHDFSFile(String path, Configuration conf) throws IOException{
        Path pt=new Path(path);
        FileSystem fs = FileSystem.get(pt.toUri(), conf);
        FSDataInputStream file = fs.open(pt);
        BufferedReader buffIn=new BufferedReader(new InputStreamReader(file));

        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = buffIn.readLine()) != null) {
            everything.append(line);
            everything.append("\n");
        }
        return everything.toString();
    }

    //TODO

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

    public static class PopularityLeagueMap extends Mapper<Text, Text, NullWritable, IntArrayWritable> {

        List<Integer> leagueSites;

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();

            String leaguePath = conf.get("league");

            String[] leagueStrings = readHDFSFile(leaguePath, conf).split("\n");
            this.leagueSites = new ArrayList<>();
            for(String league: leagueStrings){
                leagueSites.add(Integer.parseInt(league));
            }

        }


        private List<Integer[]> leagueList = new ArrayList<>();


        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

            Integer count = Integer.parseInt(value.toString());
            Integer webId = Integer.parseInt(key.toString());

            if(leagueSites.contains(webId)){
                leagueList.add(new Integer[]{webId, count});
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //TODO

            for (Integer[] item: leagueList) {
                IntArrayWritable writable = new IntArrayWritable(item);
                context.write(NullWritable.get(), writable);
            }

            //Cleanup operation starts after all mappers are finished
            //context.write(<NullWritable>, <TextArrayWritable>); // pass this output to reducer
        }
    }


    public static class PopularityLeagueReduce extends Reducer<NullWritable, IntArrayWritable, IntWritable, IntWritable> {

        @Override
        protected void setup(Context context) throws IOException,InterruptedException {
            Configuration conf = context.getConfiguration();
        }
        //TODO

        private List<Integer[]> leagueList = new ArrayList<>();

        @Override
        public void reduce(NullWritable key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
            //TODO

            for(IntArrayWritable intPair: values){
                IntWritable[] pair = (IntWritable[]) intPair.toArray();
                Integer webId = pair[0].get();
                Integer curCount = pair[1].get();

                leagueList.add(new Integer[]{webId, curCount});
            }

            //O(n^2) to get rank
            List<Integer[]> leagueRank = new ArrayList<>();
            for(Integer[] sourceLeague: leagueList){
                Integer curRank = 0;

                for(Integer[] compareLeague: leagueList){
                    if(sourceLeague[0].equals(compareLeague[0])){
                        //no compare to self
                        continue;
                    }

                    if(sourceLeague[1] > compareLeague[1]){
                        ++curRank;
                    }
                }

                leagueRank.add(new Integer[]{sourceLeague[0], curRank});
            }

            //output rank
            for(Integer[] league: leagueRank){
                IntWritable webIdWritable = new IntWritable(league[0]);
                IntWritable rankIdWritable = new IntWritable(league[1]);
                context.write(webIdWritable, rankIdWritable);
            }
        }

    }
}

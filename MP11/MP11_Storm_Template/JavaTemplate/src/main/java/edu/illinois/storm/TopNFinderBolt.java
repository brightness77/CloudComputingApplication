package edu.illinois.storm;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/** a bolt that finds the top n words. */
public class TopNFinderBolt extends BaseRichBolt {
    private OutputCollector collector;

    private int n;


    class Pair {
        public Integer count;
        public String word;

        public Pair(Integer count, String word){
            this.count = count;
            this.word = word;
        }

    }

    private PriorityQueue<Pair> pq;

    // Hint: Add necessary instance variables and inner classes if needed



    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    public TopNFinderBolt withNProperties(int N) {
        /* ----------------------TODO-----------------------
        Task: set N
        ------------------------------------------------- */
        this.n = N;

        // End
        return this;
    }

    @Override
    public void execute(Tuple tuple) {
        /* ----------------------TODO-----------------------
            Task: keep track of the top N words
            Hint: implement efficient algorithm so that it won't be shutdown before task finished
                  the algorithm we used when we developed the auto-grader is maintaining a N size min-heap
        ------------------------------------------------- */
        if(pq == null){
            pq = new PriorityQueue<>((a, b) -> a.count - b.count);
        }

        String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("count");

//        System.out.println("Dealing with tuple " + word + ": " + count);

        //check existence before add in
        Pair toRemove = null;
        for(Pair p: pq){
            if(word.equals(p.word)){
                toRemove = p;
            }
        }
        if(toRemove != null){
            pq.remove(toRemove);
        }

        pq.offer(new Pair(count, word));

        //if size exceeds, remove one
        if(pq.size() > n){
            pq.poll();
        }

        //iterate through pq
        StringBuilder sb = new StringBuilder();
        for(Pair p: pq){
            sb.append(p.word);
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2); //remove last ", "

        collector.emit(new Values("top-N", sb.toString()));
        // End
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        /* ----------------------TODO-----------------------
            Task: define output fields
            Hint: there's no requirement on sequence;
            For example, for top 3 words set ("hello", "word", "cs498"),
            "hello, world, cs498" and "world, cs498, hello" are all correct
        ------------------------------------------------- */
        declarer.declare(new Fields("top-N", "words"));
        // END
    }

}

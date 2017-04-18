//  Neil Hegde (c) 2012
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/*
 * CS61c project 1, Fall 2012.
 */
public class Proj1 {

    /** An Example Writable which contains two String Objects. */
    public static class StringPair implements Writable {
        /** The String objects I wrap. */
	private String a, b;

	/** Initializes me to contain empty strings. */
	public StringPair() {
	    a = b = "";
	}
	
	/** Initializes me to contain A, B. */
        public StringPair(String a, String b) {
            this.a = a;
	    this.b = b;
        }

        /** Serializes object - needed for Writable. */
        public void write(DataOutput out) throws IOException {
            new Text(a).write(out);
	    new Text(b).write(out);
        }

        /** Deserializes object - needed for Writable. */
        public void readFields(DataInput in) throws IOException {
	    Text tmp = new Text();
	    tmp.readFields(in);
	    a = tmp.toString();
	    
	    tmp.readFields(in);
	    b = tmp.toString();
        }

	/** Returns A. */
	public String getA() {
	    return a;
	}
	/** Returns B. */
	public String getB() {
	    return b;
	}
    }


  /**
   * Inputs a set of (docID, document contents) pairs.
   * Outputs a set of (Text, Text) pairs.
   */
    public static class Map1 extends Mapper<WritableComparable, Text, Text, StringPair> {
        /** Regex pattern to find words (alphanumeric + _). */
        final static Pattern WORD_PATTERN = Pattern.compile("\\w+");

        private String targetGram = null;
	private int funcNum = 0;
	private int numWords = 1;
      
        /*
         * Setup gets called exactly once for each mapper, before map() gets called the first time.
         * It's a good place to do configuration or setup that can be shared across many calls to map
         */
        @Override
        public void setup(Context context) {
            targetGram = context.getConfiguration().get("targetGram").toLowerCase();
	    for ( char character : targetGram.toCharArray()){
		if (character == ' '){
		    numWords++;
		}

	    }
	    try {
		funcNum = Integer.parseInt(context.getConfiguration().get("funcNum"));
	    } catch (NumberFormatException e) {
		/* Do nothing. */
	    }
        }

        @Override
        public void map(WritableComparable docID, Text docContents, Context context)
                throws IOException, InterruptedException {
            Matcher matcher = WORD_PATTERN.matcher(docContents.toString());
	    Func func = funcFromNum(funcNum);
	    int pos = 0;	 	// Position in ArrayList
	    int count = 1;	 	//Counter for number of words concatenated into an n-gram  
	    String holder = "";
	    ArrayList<String> wordList = new ArrayList<String>();
	    ArrayList<String> ngramList = new ArrayList<String>();
	    ArrayList<Integer> ngramPosList = new ArrayList<Integer>();
	    PriorityQueue<Integer> sortedTargetGramPositions = new PriorityQueue<Integer>();
	    
	    parse:
            while (matcher.find()) {
		wordList.add(matcher.group().toString().toLowerCase());
            }

	  
	    int len = wordList.size();
	    while ( (pos + numWords) <= len){
		holder = wordList.get(pos);

		while ( count < (numWords)){
		    holder =  holder + " " + wordList.get((pos + count));
		    count++;

		}

		ngramList.add(holder); //Could have tested if TargetGram == a
		ngramPosList.add(pos);

		if(targetGram.equals(holder) ){
		    sortedTargetGramPositions.add(pos);
		}
		pos++;
		count = 1;
	    }

	    if (sortedTargetGramPositions.peek() == null) {
		for ( String word : ngramList ) {
		    context.write(new Text(word), new StringPair(Double.toString(func.f(Double.POSITIVE_INFINITY)), "1"));
		}
	    } else {
	        Integer[] sTGP =  new Integer[(sortedTargetGramPositions.size())];
	        sortedTargetGramPositions.toArray(sTGP);

	        len = ngramList.size();
	        pos = 0;
	        int minDistance = 0;
	        int temp = 0;
	        while ( pos < len){
	    	    String currNgram = ngramList.get(pos);
	    	    if (!(targetGram.equals(currNgram))) {
		        minDistance = Integer.MAX_VALUE;
		        count = ngramPosList.get(pos);
		        for ( int p : sTGP ){
			    temp = Math.abs(p - pos);
			    if ( minDistance > temp) {
			        minDistance = temp;
			    }
		        }
		        context.write(new Text(currNgram), new StringPair(Double.toString(func.f(minDistance)), "1"));
		    }
		    pos++;
		}
	    }
	}
    

	/** Returns the Func corresponding to FUNCNUM*/
	private Func funcFromNum(int funcNum) {
	    Func func = null;
	    switch (funcNum) {
	    case 0:	
		func = new Func() {
			public double f(double d) {
			    return d == Double.POSITIVE_INFINITY ? 0.0 : 1.0;
			}			
		    };	
		break;
	    case 1:
		func = new Func() {
			public double f(double d) {
			    return d == Double.POSITIVE_INFINITY ? 0.0 : 1.0 + 1.0 / d;
			}			
		    };
		break;
	    case 2:
		func = new Func() {
			public double f(double d) {
			    return d == Double.POSITIVE_INFINITY ? 0.0 : Math.sqrt(d);
			}			
		    };
		break;
	    }
	    return func;
	}
    }
    /** Here's where you'll be implementing your combiner. It must be non-trivial for you to receive credit. */
    public static class Combine1 extends Reducer<Text, StringPair, Text, StringPair> {

	//      @Override Commented out because this method does not override or implement a method in Reducer
      public void reduce(Text key, Iterable<StringPair> values,
              Context context) throws IOException, InterruptedException {
	  int modG = 0;
	  double valueTotal = 0.0;
	  for ( StringPair s : values) {
	      valueTotal += Double.parseDouble(s.getA());
	      modG += Integer.parseInt(s.getB());
	  }
	  context.write(key, new StringPair( Double.toString(valueTotal), Integer.toString(modG) ));
      }
    }


    public static class Reduce1 extends Reducer<Text, StringPair, DoubleWritable, Text> {
	//        @Override Commented out because this method does not override or implement a method in Reducer
        public void reduce(Text key, Iterable<StringPair> values,
			   Context context) throws IOException, InterruptedException {	  
	  double sum = 0.0;
	  int modG = 0;
	  double output = 0.0;
	  for( StringPair s : values){
	      sum += Double.parseDouble(s.getA());
	      modG += Integer.parseInt(s.getB());
	  }
	  if (sum > 0) {
	      output = (sum * (Math.pow((Math.log(sum)), 3.0)) / modG);
	  } //else output is 0.0
	  context.write(new DoubleWritable(((-1.0)*output)), key);
        }
    }

    public static class Map2 extends Mapper<DoubleWritable, Text, DoubleWritable, Text> {
	//Don't Need
    }

    public static class Reduce2 extends Reducer<DoubleWritable, Text, DoubleWritable, Text> {

      int n = 0;
      static int N_TO_OUTPUT = 100;

      /*
       * Setup gets called exactly once for each reducer, before reduce() gets called the first time.
       * It's a good place to do configuration or setup that can be shared across many calls to reduce
       */
      @Override
      protected void setup(Context c) {
      }

        @Override
        public void reduce(DoubleWritable key, Iterable<Text> values,
                Context context) throws IOException, InterruptedException {
	    for ( Text t : values ) {
		/*		if ( n > N_TO_OUTPUT ){
			break;
			}*/
		key.set((Math.abs(Double.parseDouble(key.toString()))));
		context.write(key, t);
		//		n++;
		    
	    }
	}
    }

    /*
     *  You shouldn't need to modify this function much. If you think you have a good reason to,
     *  you might want to discuss with staff.
     *
     *  The skeleton supports several options.
     *  if you set runJob2 to false, only the first job will run and output will be
     *  in TextFile format, instead of SequenceFile. This is intended as a debugging aid.
     *
     *  If you set combiner to false, neither combiner will run. This is also
     *  intended as a debugging aid. Turning on and off the combiner shouldn't alter
     *  your results. Since the framework doesn't make promises about when it'll
     *  invoke combiners, it's an error to assume anything about how many times
     *  values will be combined.
     */
    public static void main(String[] rawArgs) throws Exception {
        GenericOptionsParser parser = new GenericOptionsParser(rawArgs);
        Configuration conf = parser.getConfiguration();
        String[] args = parser.getRemainingArgs();

        boolean runJob2 = conf.getBoolean("runJob2", true);
        boolean combiner = conf.getBoolean("combiner", false);

        if(runJob2)
          System.out.println("running both jobs");
        else
          System.out.println("for debugging, only running job 1");

        if(combiner)
          System.out.println("using combiner");
        else
          System.out.println("NOT using combiner");

        Path inputPath = new Path(args[0]);
        Path middleOut = new Path(args[1]);
        Path finalOut = new Path(args[2]);
        FileSystem hdfs = middleOut.getFileSystem(conf);
        int reduceCount = conf.getInt("reduces", 32);

        if(hdfs.exists(middleOut)) {
          System.err.println("can't run: " + middleOut.toUri().toString() + " already exists");
          System.exit(1);
        }
        if(finalOut.getFileSystem(conf).exists(finalOut) ) {
          System.err.println("can't run: " + finalOut.toUri().toString() + " already exists");
          System.exit(1);
        }

        {
            Job firstJob = new Job(conf, "wordcount+co-occur");

            firstJob.setJarByClass(Map1.class);

	    /* You may need to change things here */
            firstJob.setMapOutputKeyClass(Text.class);
            firstJob.setMapOutputValueClass(StringPair.class);
            firstJob.setOutputKeyClass(DoubleWritable.class);
            firstJob.setOutputValueClass(Text.class);
	    /* End region where we expect you to perhaps need to change things. */

            firstJob.setMapperClass(Map1.class);
            firstJob.setReducerClass(Reduce1.class);
            firstJob.setNumReduceTasks(reduceCount);


            if(combiner)
              firstJob.setCombinerClass(Combine1.class);

            firstJob.setInputFormatClass(SequenceFileInputFormat.class);
            if(runJob2)
              firstJob.setOutputFormatClass(SequenceFileOutputFormat.class);

            FileInputFormat.addInputPath(firstJob, inputPath);
            FileOutputFormat.setOutputPath(firstJob, middleOut);

            firstJob.waitForCompletion(true);
        }

        if(runJob2) {
            Job secondJob = new Job(conf, "sort");

            secondJob.setJarByClass(Map1.class);
	    /* You may need to change things here */
            secondJob.setMapOutputKeyClass(DoubleWritable.class);
            secondJob.setMapOutputValueClass(Text.class);
            secondJob.setOutputKeyClass(DoubleWritable.class);
            secondJob.setOutputValueClass(Text.class);
	    /* End region where we expect you to perhaps need to change things. */

            secondJob.setMapperClass(Map2.class);
            if(combiner)
              secondJob.setCombinerClass(Reduce2.class);
            secondJob.setReducerClass(Reduce2.class);

            secondJob.setInputFormatClass(SequenceFileInputFormat.class);
            secondJob.setOutputFormatClass(TextOutputFormat.class);
            secondJob.setNumReduceTasks(1);


            FileInputFormat.addInputPath(secondJob, middleOut);
            FileOutputFormat.setOutputPath(secondJob, finalOut);

            secondJob.waitForCompletion(true);
        }
    }

}

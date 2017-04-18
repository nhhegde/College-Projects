/**
 * TransactionHistogram
 *
 * Object encapsulating the histogram interactions for all transactions. This
 * visualization will show a histogram of the contribution sizes for the list
 * of contributions that the render method is given.
 *
 * @constructor
 * @params {d3.selection} selector
 * @params {d3.dispatch} dispatch
 */
var TransactionHistogram = function (selector, dispatch) {
    this.dispatch = dispatch;
    this.selector = selector;

    this.first = false;
    // Viz parameters
    this.height = 450;
    this.width = 330;
    var marginBottom = 20;

    // Create histogram svg container
    this.svg = this.selector.append('svg')
        .attr('width', this.width)
        .attr('height', this.height + marginBottom)  // space needed for ticks
        .append('g');

    // Bin counts
    this.bins = [50, 200, 500, 1000, 10000, 50000, 100000, 1000000];
    this.histogramLayout = d3.layout.histogram()
      .bins([0].concat(this.bins))
      .value(function(d) {return d.amount;});

    // Initialize default color
    this.setHistogramColor(this.colorStates.DEFAULT);
};

/**
 * render(data)
 *
 * Given a data list, histogram the contribution amounts. The visualization will
 * show how many contributions were made between a set of monetary ranges defined
 * by the buckets `this.bins`.
 *
 * The data is expected in the following format:
 *     data = [
 *        {'state': 'CA', 'amount': 200},
 *        {'state': 'CA', 'amount': 10},
 *        ...
 *     ]
 *
 * @params {Array} data is an array of objects with keys 'state' and 'amount'
 */

TransactionHistogram.prototype.trns = function(d) {

    return "translate(" + this.xScale(d.x) + "," + this.yScale(d.y) + ")"; 
}

TransactionHistogram.prototype.render = function(data) {
    // Needed to access the component in anonymous functions
    var that = this;

    // Generate the histogram data with D3
    var histogramData = this.histogramLayout(data);
    if (!this.hasScaleSet()) {
        histogramData = this.setScale(data);
    } else {
        histogramData = this.histogramLayout(data);
    }

    /** Histogram visualization setup **/
    // Select the bar groupings and bind to the histogram data
    var bar = this.svg.selectAll('.bar')
          .data(histogramData, function(d) {return d.x;});
    xScale2 = that.xScale;
    yScale2 = that.yScale;
    /*
    var y = this.yScale;
    var x = this.xScale;
    var xTicks = this.xTicks;
    var xAxis = this.xAxis;*/
    var height2 = that.height;
    var fillColor = '#E98851'


    
    if(!that.first) {

        that.first = true;
        
        var barEnter = bar.enter()
          
        .append("g")
        .attr("class", "bar")
        .attr("transform", function(d) {
            var told = height2 - yScale2(d.y);
            return "translate(" + xScale2(d.x) + "," + told + ")"; });

        barEnter.append("rect")
        .attr("fill", '#FFFFFF')
        .transition().duration(1000)
        .attr("x", 1)
        .attr("width", xScale2(histogramData[0].dx) - 1)
        .attr("fill", that.currentColorState) //orange: '#E98851' , green '#1976D2';
        
        .attr("height", function(d) { return yScale2(d.y);}); //height2 - 
        

        barEnter.append("text")
        .attr("dy", ".75em")
        .attr("y", 6)
        .attr("x", xScale2(histogramData[0].dx) / 2)
        .attr("text-anchor", "middle")
        .text(function(d) { return d.y; });

    } else {


        bar
        .transition().duration(1000)
        .attr("transform", function(d) {
            var told = height2 - yScale2(d.y);
            return "translate(" + xScale2(d.x) + "," + told + ")"; })

        .select("rect")
        .attr("x", 1)
        .attr("width", xScale2(histogramData[0].dx) - 1)
        .attr("fill", that.currentColorState) //orange: '#E98851' , green '#1976D2';
        .attr("height", function(d) { return yScale2(d.y);});
        //.duration(500); //height2 - 
        bar.select("text")
        .attr("dy", ".75em")
        .attr("y", 6)
        .attr("x", xScale2(histogramData[0].dx) / 2)
        .attr("text-anchor", "middle")
        .text(function(d) { return d.y; });
    }
    
    /* Enter phase */
    // Implement
    // Add a new grouping

    // Add a rectangle to this bar grouping

    // Add text to this bar grouping


    /** Update phase */
    // Implement


    /** Exit phase */
    bar.exit().remove();


    // Draw / update the axis as well
    this.drawAxis();

};


/**
 * formatBinCount(number)
 *
 * Helper function for formatting the bin count.
 * @params {number} number
 * @return number
 */
TransactionHistogram.prototype.formatBinCount = function (number) {
    if (number < 100)
      return number;
    return d3.format('.3s')(number);
};

/*
 * Helper function for determing the position of text.
 *
 * @params {Object} a single transaction/state object
 * @return {boolean} true if the text should go below the bar
 */
TransactionHistogram.prototype.isBarLargeEnough = function (d) {
    var yCoordinate = (d.y === 0) ? 0 : this.yScale(d.y);
    return (this.height - yCoordinate) > 12;
};


/*
 * drawAxis()
 *
 * Draws the x-axis for the histogram.
 */
TransactionHistogram.prototype.drawAxis = function() {
    // Add the X axis
    var xAxis = d3.svg.axis()
        .scale(this.xScale)
        .orient("bottom")
        .tickFormat(d3.format('$.1s'));

    // Add ticks for xAxis (rendering the xAxis bar and labels)
    if (this.xTicks === undefined) {
      this.xTicks = this.svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + this.height + ")");
    }
    this.xTicks.transition().duration(500).call(xAxis);
};


/*
 * setScale(data)
 *
 * Sets the X and Y scales for the histogram. Based on the `data` provided.
 *
 * @params {Array} data is an array of objects with keys 'state' and 'amount'
 * @return {Array} returns histogramData so that recomputation of buckets not necesary
 */
TransactionHistogram.prototype.setScale = function (data) {
    var histogramData = this.histogramLayout(data);

    this.xScale = d3.scale.threshold()
      .domain(this.bins)
      .range(d3.range(0, this.width, this.width/(this.bins.length + 1)));

    // Implement: define a suitable yScale given the data
    this.yScale = d3.scale.sqrt()
        .domain([
            0,
            d3.max(histogramData, function(d) { return d.length;})
        ])
        .range([0, this.height]);

    return histogramData;
};

/*
 * hasScaleSet()
 *
 * Checks if the scale has already been set.
 *
 * @return {boolean} true if the x and y scales have been already set.
 */
TransactionHistogram.prototype.hasScaleSet = function () {
    return this.xScale !== undefined && this.yScale !== undefined;
};

TransactionHistogram.prototype.colorStates = {
    'PRIMARY': '#E98851',
    'SECONDARY': '#36d387',
    'DEFAULT': '#E8F5E9'
}


/*
 * setBarColor()
 *
 * Set the color mode of the bar.
 */
TransactionHistogram.prototype.setHistogramColor = function (colorState) {
    this.currentColorState = colorState;
};

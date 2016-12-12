// JQuery Code
$(document).ready(function(){

    var filter = 1;
    var dataFile = 'data/mostStories.json';

    // Function Call to get API
    //getData();
    var dataJson;
    contructGraph(filter, dataFile);

    // Handle Filter for Directed Graph
    $('.key ul li a').click(function() {

        // Remove Marker
        $('#' + filter).removeClass('filter_selected');

        filter = $(this).attr('id');
       
        // Add Marker
        $('#' + filter).addClass('filter_selected');

        // Destroy Graph
        $('#directed_graph #graph svg').html('');
        $('#directed_graph #legend').html('');

        // Function Call to get API
        //getData();
        // Call function to form the graph
        contructGraph(filter, dataFile);
    });


    // Handle File for Directed Graph
    $('.time ul a').click(function() {

        dataFile = $(this).attr('val');

        // Send value to iframe
        // Parallel Co-ordiante Iframe
        iframe = document.getElementById('parallel_coord_graph').contentWindow;
        iframe.postMessage(dataFile, "https://niharikasharma.github.io/fanfiction/parallelcoord.html");


        // Destroy Graph
        $('#directed_graph #graph svg').html('');
        $('#directed_graph #legend').html('');

        // Function Call to get API
        //getData();
        // Call function to form the graph
        contructGraph(filter, dataFile);

    });


    // Function to Call API - AJAX
    function getData() {

        var fi = 'Leafy Slader';
        var post_data = "{user1: " + fi + "}";

        $.ajax({
            type: 'POST',
            url: 'http://35.161.81.55:8080/userData',
            contentType: "application/json",
            data: post_data,

            success: function(data) {

                // API Success
                var dataJson = data;

                // Call function to form the graph
                //contructGraph(filter, dataJson);
            },

            error: function() {
                
                // Handle Failure
            }
        });
    }




    // Function to form the graph
    function contructGraph(filter, dataFile) {

        // Check filter 
        var width = 824;
        var height = 512;

        var directed_svg = d3.select('#graph svg')
            .attr('width', width)
            .attr('height', height);


        var color = d3.scaleOrdinal(d3.schemeCategory20);

        var radius = 5;

        var i = 30;
        // var simulation = d3.forceSimulation()
        //     .force("link", d3.forceLink().id(function(d) { return d.id; }).distance(i).strength(2))
        //     .force("charge", d3.forceManyBody())
        //     .force("collide",d3.forceCollide( function(d){return 10; }) )
        //     .force("y", d3.forceY(0))
        //     .force("x", d3.forceX(0))
        //     .force("center", d3.forceCenter(width/1.75, height/1.75));

        var i = 50;
        var simulation = d3.forceSimulation()
            .force("link", d3.forceLink().id(function(d) { return d.id; }).distance(i).strength(1))
            .force("charge", d3.forceManyBody())
            .force("y", d3.forceY(0))
            .force("x", d3.forceX(0))
            .force("center", d3.forceCenter(width/1.75, height/1.75));

        //console.log(simulation);

        // Project Graph
        d3.json(dataFile, function(error, graph) {

            if (error) throw error;

             var filter_cat;
            if(filter == 1) {
                filter_cat = function(d) { return color(d.fandom); };
            } else if (filter == 2) {
                filter_cat = function(d) { return color(d.category); };
            } else if (filter == 3) {
                filter_cat = function(d) { return color(d.lang); };
            } else if (filter == 4) {
                filter_cat = function(d) { return color(d.rating); };
            } else if (filter == 5) {
                filter_cat = function(d) { return color(d.storyRange); };
            } else {
                filter_cat = function(d) { return color(d.type); };
            }


            var nodeScale = d3.scaleLinear()
                                .domain([0, d3.max(graph.nodes, function(d){return d.story; })])
                                .rangeRound([4, 10]);


            /********** Legend Weight ************/
            var test = d3.extent(graph.nodes, function(d) { return d.story; });
            console.log(test);

            //Add legend for size/weight
            var weights = directed_svg.append('g')
              .attr('class', 'legend weight')
              .selectAll('circle')
              .data(test)
              .enter().append('g')
              .attr('transform', function(d, i) { return "translate(0, " + (i * 25) + ")"; });

            weights.append("circle")
                  .attr("cx", width - 650)
                  .attr("cy", 450)
                  .attr("r", nodeScale)
                  .style("fill", 'gray');

            weights.append("text")
                  .attr("x", width - 675)
                  .attr("y", 450)
                  .attr("dy", ".4em")
                  .style("text-anchor", "end")
                  .text(function(d) { return d + " stories"; });

            /********** Legend Weight ************/
            var link = directed_svg.append("g")
                        .attr("class", "links")
                        .selectAll("line")
                        .data(graph.links)
                        .enter()
                        .append("line")
                        .attr("stroke-width", function(d) { return Math.sqrt(d.value); });

            var node = directed_svg.append("g")
                        .attr("class", "nodes")
                        .selectAll("circle")
                        .data(graph.nodes)
                        .enter().append("circle")
                        .attr("r", function(d){
                            return nodeScale(d.story);})
                        .attr("fill", filter_cat)
                        .call(d3.drag()
                        .on("start", dragstarted)
                        .on("drag", dragged)
                        .on("end", dragended))
                        .text(function(d) { return d.id; });

            

            node.append("title")
                .text(function(d) { return (
                    'User - ' + d.id + 
                    '\nType - ' + d.type + 
                    '\nfandom -' + d.fandom +
                    '\nAverage Reviews - ' + d.avgReview + 
                    '\nTotal Reviews - ' + d.ttlReview + 
                    '\nRating - ' + d.rating + 
                    '\nRating Description - ' + d.ratingDescription + 
                    '\nCategory - ' + d.category + 
                    '\nLanguage - ' + d.lang + 
                    '\nTotal Number of words written - ' + d.ttlWords + 
                    '\nAverage Number of words written per story- ' + d.avgWords + 
                    '\nAverage Number of followers - ' + d.avgFollowers + 
                    '\nNumber of stories - ' + d.story +' ('+ d.storyRange + ')' + 
                    '\nNumber of Favorites (avg.) - ' + d.avgFav); 
            });

            simulation
                .nodes(graph.nodes)
                .on("tick", ticked);

            simulation.force("link")
                .links(graph.links);

            function ticked() {

                node.attr("cx", function(d) { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
                    .attr("cy", function(d) { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });

                link.attr("x1", function(d) { return d.source.x; })
                    .attr("y1", function(d) { return d.source.y; })
                    .attr("x2", function(d) { return d.target.x; })
                    .attr("y2", function(d) { return d.target.y; });
            }



            // Add legend for color/origin
            // Check filter 
            var lwidth = 200;
            var lheight = 512;

            var legend_svg = d3.select('#legend');

            var legend = legend_svg.append("g")
                                .attr("class", "legend")
                                .selectAll("circle")
                                .data(color.domain())
                                .enter()
                                    .append("svg")
                                    .attr("height", 20)
                                    .attr("width", 200)
                                    .attr("transform", function(d, i) { 
                                        if(d != '' || d == '0') {
                                            return "translate(0," + i * 20 + ")"; 
                                        }
                                    });


            legend.append("circle")
                    .attr("cx", lwidth - 20)
                    .attr("cy", 9)
                    .attr("r", 5)
                    .style("fill", color);

            legend.append("text")
                    .attr("x", lwidth - 30)
                    .attr("y", 9)
                    .attr("dy", ".35em")
                    .style("text-anchor", "end")
                    .text(
                        function(d) {
                            if(d.length > 20) {
                                return d.substring(0, 20);
                            } else {
                                return d; 
                            }
                    });
        });



        function dragstarted(d) {
            if (!d3.event.active) simulation.alphaTarget(0.3).restart();
            d.fx = d.x;
            d.fy = d.y;
        }

        function dragged(d) {
            d.fx = d3.event.x;
            d.fy = d3.event.y;
        }

        function dragended(d) {
            if (!d3.event.active) simulation.alphaTarget(0);
            d.fx = null;
            d.fy = null;
        }
    }
});




var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/results', function (greeting) {
            var body = JSON.parse(greeting.body);
            if (body.success) {
                showResults(body.id, body.value);
            } else {
                showResults(body.id, "Error: " + body.errorMessage)
            }
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendAdd() {
    var operationId = Date.now();
    stompClient.send("/app/compute", {}, JSON.stringify({'left': $("#left").val(),
        'right': $("#right").val(),
        'operand': 'ADD',
        'sleep': $("#sleep").val(),
        'id': operationId
    }));
    $("#results").append("<tr><td>" + $("#left").val() + " + " + $("#right").val() +
        "<span id="+ operationId +"/>" +
        "</td><td><button onclick=\"requestResult(\'"+ operationId+ "\')\">Request Result</button>" +
        "</td></tr>");

}

function requestResult(id) {
    $.ajax({
        type: "GET",
        url: "result/"+id,
        success: function(result) {
            if (result) {
                if (result.success) {
                    $("#" + result.id).append(" = " + result.value);
                }
            }
        },
        error: function(result) {
        }
    });
}

function showResults(id, message) {
    $("#"+id).append(" = " +message);
    $.ajax({
        type: "POST",
        url: "result/ack/"+id,
        success: function(result) {
        },
        error: function(result) {
        }
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendAdd(); });
});
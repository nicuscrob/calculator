var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
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
                showResults(body.value);
            } else {
                showResults("Error: " + body.errorMessage)
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
    stompClient.send("/app/compute", {}, JSON.stringify({'left': $("#left").val(),
        'right': $("#right").val(),
        'operand': 'ADD',
        'sleep': $("#sleep").val()
    }));
}

function showResults(message) {
    $("#results").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendAdd(); });
});
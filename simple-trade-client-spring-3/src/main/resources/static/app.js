const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/trade-websocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', (message) => {
        const myMsg = JSON.parse(message.body)
        showMessage(JSON.stringify(myMsg));
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#messages-table").show();
    }
    else {
        $("#messages-table").hide();
    }
    $("#messages").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendOrderCancel() {
    stompClient.publish({
        destination: "/app/order-cancel",
        body: JSON.stringify(
            {
                'msgType': "F",
                'origClOrdID': $("#orderCancelOrigClOrdID").val(),
                'clientID': $("#orderCancelClientID").val(),
                'symbol': $("#orderCancelSymbol").val(),
                'side': $("#orderCancelSide-select").val(),
                'qty': $("#orderCancelQty").val(),
            })
    });
}

function sendOrderCancelReplaceRequest() {
    // In this example ClOrdId and TransactTime are populated by the consumer of this message
    // OrigClOrdID must however be supplied
    stompClient.publish({
        destination: "/app/order-cancel-replace-request",
        body: JSON.stringify(
            {
                'msgType': "G",
                'origClOrdID': $("#orderCancelReplaceRequestOrigClOrdID").val(),
                'clientID': $("#orderCancelReplaceRequestClientID").val(),
                'symbol': $("#orderCancelReplaceRequestSymbol").val(),
                'side': $("#orderCancelReplaceRequestSide-select").val(),
                'qty': $("#orderCancelReplaceRequestQty").val(),
                'ordType': $("#orderCancelReplaceRequestOrd-type-select").val(),
                'px': $("#orderCancelReplaceRequestPx").val(), // price is required if a limit order
                'tif': $("#orderCancelReplaceRequestTif-select").val()
            })
    });
}


function sendNewOrderSingle() {
    // In this example ClOrdId and TransactTime are populated by the consumer of this message
    stompClient.publish({
        destination: "/app/new-order-single",
        body: JSON.stringify(
            {'msgType': "D",
                'symbol': $("#symbol").val(),
                'qty': $("#qty").val(),
                'px': $("#px").val(),
                'clientID': $("#clientID").val(),
                'side': $("#side-select").val(),
                'ordType': $("#ord-type-select").val(),
                'tif': $("#tif-select").val()})
    });
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#sendNewOrderSingle" ).click(() => sendNewOrderSingle());
    $( "#sendOrderCancel" ).click(() => sendOrderCancel());
    $( "#sendOrderCancelReplaceRequest" ).click(() => sendOrderCancelReplaceRequest());
});


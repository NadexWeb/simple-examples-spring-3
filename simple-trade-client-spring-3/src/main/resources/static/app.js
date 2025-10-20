const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/trade-websocket'
});

// so we need capability to use the working orders list to get the data to support the
// order cancel and order cancel replace request funtionality

// I image that the working orders could be in a table with a choice to cancel or update the working order

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/messages', handleMessage)
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function handleMessage(message) {
    const myMsg = JSON.parse(message.body)


    if (myMsg.msgType === '8' && myMsg.execType !== 'I') {  // exec report and not result of order status request
        if (myMsg.ordStatus === '0' || myMsg.ordStatus === '1') {
            handleWorkingOrderAdd(myMsg);
        // any other states, the order is no longer working and should be removed from working orders
        } else if (!['6', '8', 'A', 'B', 'D', 'E'].includes(myMsg.ordStatus)) {
            handleWorkingOrderRemove()
        }
    }
    showMessage(JSON.stringify(myMsg));
}

function handleWorkingOrderRemove() {}

// these are working order states
// put in working orders with the important fields
// we would use for order cancel or order cancel replace request
// Symbol
// Original Client Order ID
// Order Quantity
// Price
// Client ID
// Side
// Order Type
// Time In Force
function handleWorkingOrderAdd(order) {
    const rowHtml = buildHtmlRow(order)
    $('#working-orders').prepend(rowHtml)
}

const workingOrderRowId = (id) => `wo-${id}`

function buildHtmlRow(order) {
    const {
        msgType,
        origClOrdID,
        clientID,
        symbol,
        side,
        qty,
        ordType,
        px,
        tif,
    } = order;
    const fields = [symbol, msgType, origClOrdID, qty, px, clientID, side, ordType, tif ]
    const cells = fields.map(buildHtmlCell)
    const rowTemplate = `<tr id=${workingOrderRowId(origClOrdID)}>${cells}</tr>`
    return rowTemplate
}

function buildHtmlCell({ id, value}) {
    return `<td id=${id} class='p-2 text-center'>${value}</td>`
}

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
    // TODO add outbound messages to table
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
    // TODO add outbound messages to table
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
    // TODO add outbound messages to table
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

function removeMessage(orderId) {
    $(workingOrderRowId(orderId)).remove();
}

function showMessage(message) {
    $("#messages").append("<li>" + message + "</li>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#sendNewOrderSingle" ).click(() => sendNewOrderSingle());
    $( "#sendOrderCancel" ).click(() => sendOrderCancel());
    $( "#sendOrderCancelReplaceRequest" ).click(() => sendOrderCancelReplaceRequest());
});


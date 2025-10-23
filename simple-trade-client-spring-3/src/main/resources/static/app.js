/* Simple Working Order submission example

    Developer note:
        - uses JQuery - all the DOM manipulation methods you see such as $() are not native browser APIs.
        - uses tailwindCSS for styling. This provides composable helper classes which you add to elements to style them.
*/

class WebSocketManager {
  client = null;
  isConnected = false;
  socketState = 'INACTIVE';

  constructor() {
    this.client = new StompJs.Client({
      brokerURL: 'ws://localhost:8080/trade-websocket',
    });

    this.client.onConnect = (frame) => {
      this.isConnected = true;
      console.log('Connected: ' + frame);
      window.wsControlsController.setSocketStatusIndicator('ACTIVE');
      window.newWorkingOrderTicketController.enableSubmitBtn();
      this.client.subscribe('/topic/messages', this._handleMessage.bind(this));
    };

    this.client.onDisconnect = () => {
      this.isConnected = false;
      window.wsControlsController.setSocketStatusIndicator('INACTIVE');
      window.newWorkingOrderTicketController.disableSubmitBtn();
    };

    this.client.onWebSocketError = (error) => {
      console.error('Error with websocket', error);
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };
  }

  connect() {
    console.log('Socket connecting...');
    this.client.activate();
  }

  sendMessage(destination, body) {
    const jsonBody = JSON.stringify(body);
    console.log(`sending message\nto: ${destination}\nbody: ${jsonBody}`);
    this.client.publish({ destination, body: jsonBody });
  }

  async disconnect() {
    console.log('Socket disconnecting...');
    await this.client.deactivate();
    this.isConnected = false;
  }

  _handleMessage(message) {
    const parsedMessage = JSON.parse(message.body);
    console.log('message received: ', parsedMessage);
    window.messagesController.pushMessage('INBOUND', message.body);

    if (parsedMessage.msgType === '8') {
      this._handleExecutionReport(parsedMessage);
    }
  }

  _handleExecutionReport(message) {
    if (message.execType !== 'I') {
      // exec report and not result of order status request
      if (message.ordStatus === '0') {
        window.workingOrdersManager.newOrder(message);
      } else if (message.ordStatus === '1') {
        // PARTIAL FILL
        window.workingOrdersManager.orderFilled(message);
      } else if (message.ordStatus === '2') {
        // FILLED
        window.workingOrdersManager.partialFill(message);
      } else if (message.ordStatus === '4') {
        // CANCELLED
        window.workingOrdersManager.orderCancelled(message);
      } else if (message.ordStatus === '5') {
        // REPLACED
        window.workingOrdersManager.orderReplaced(message);
      }
    }
  }
}

class WorkingOrdersManager {
  constructor() {
    this.clientID = null;
    this.workingOrders = {};
  }

  newOrder(order) {
    this.workingOrders[order.orderID] = order;
    window.workingOrdersTableController.addRow(order);
  }
  orderFilled(order) {
    window.workingOrdersTableController.removeRow(order.orderID);
    delete this.workingOrders[order.orderID];
  }
  partialFill(order) {
    console.error('Not implemented!');
    window.workingOrdersTableController.replaceRow(order);
    // TODO: update this.workingOrders
  }
  orderCancelled(order) {
    window.workingOrdersTableController.removeRow(order.orderID);
    delete this.workingOrders[order.orderID];
  }
  orderReplaced(order) {
    console.error('Not implemented!');
    window.workingOrdersTableController.replaceRow(order);
    // TODO: update this.workingOrders
  }
}

class WorkingOrdersTableController {
  workingOrdersTable = null;

  constructor(workingOrdersTableSelector) {
    this.workingOrdersTable = $(workingOrdersTableSelector);
  }

  addRow({
    symbol,
    orderID,
    orderQty,
    price,
    clientOrderID,
    side,
    timeInForce, // TODO: this is not present on message
    orderType, // TODO: this is not present on message
  }) {
    const fields = [
      new Date().toLocaleTimeString(),
      symbol,
      clientOrderID,
      orderID,
      orderQty,
      price,
      window.workingOrdersManager.clientID,
      tradeSides[side],
      timeInForce ?? '-',
      orderType ?? '-',
    ];
    const cells = fields.map(
      (value) =>
        `<td class='px-1 text-center text-xs overflow-hidden truncate' title="${value}">${value}</td>`
    );
    cells.push(`<td class='text-red-500 font-bold px-2 py-1 text-left text-xs'>
			<button
				class='cursor-pointer p-1'
				onclick="window.cancelWorkingOrderTicketController.newTicket.call(window.cancelWorkingOrderTicketController, '${orderID}')"
				>Cancel</button>
		</td>`);
    cells.push(`<td class='text-[#00aeef] font-bold px-2 py-1 text-left text-xs'>
			<button
				class='cursor-pointer p-1'
				onclick="window.replaceWorkingOrderTicketController.newTicket.call(window.replaceWorkingOrderTicketController, '${orderID}')"
				>Edit</button
		</td>`);
    const rowHtml = `<tr id=${workingOrderRowId(orderID)}>${cells}</tr>`;
    this.workingOrdersTable.append(rowHtml);
  }

  removeRow(orderId) {
    const tableRowId = workingOrderRowId(orderId);
    document.querySelector(`#${tableRowId}`).remove();
  }

  replaceRow() {
    console.log(...arguments);
  }
}

class MessagesListController {
  messagesList = null;

  constructor(messagesListSelector) {
    this.messagesList = $(messagesListSelector);
  }

  pushMessage(channel, message) {
    const className = channel === 'INBOUND' ? '' : 'bg-green-200';
    const messageHtml = `<strong>${new Date().toLocaleTimeString()} - ${channel}</strong></br> ${JSON.stringify(
      message
    )}`;

    this.messagesList.prepend(
      `<li class="${className} py-1">${messageHtml}</li>`
    );
  }
}

class WSControlsController {
  connectBtn = null;
  disconnectBtn = null;
  wsStatusIndicator = null;

  constructor(connectBtnSelector, disconnectBtnSelector, wsStatusSelector) {
    this.connectBtn = $(connectBtnSelector);
    this.disconnectBtn = $(disconnectBtnSelector);
    this.wsStatusIndicator = $(wsStatusSelector);

    this.connectBtn.on('click', this.handleConnectClicked.bind(this));
    this.disconnectBtn.on('click', this.handleDisconnectClicked.bind(this));
  }

  handleConnectClicked() {
    this.connectBtn.prop('disabled', true);
    this.disconnectBtn.prop('disabled', false);
    window.websocketManager.connect();
  }

  setSocketStatusIndicator(state) {
    switch (state) {
      case 'ACTIVE':
        this.wsStatusIndicator.html('Connected');
        this.wsStatusIndicator.removeClass('text-red-500');
        this.wsStatusIndicator.addClass('text-green-500');
        return;
      default:
        this.wsStatusIndicator.html('Disconnected');
        this.wsStatusIndicator.removeClass('text-green-500');
        this.wsStatusIndicator.addClass('text-red-500');
        return;
    }
  }

  async handleDisconnectClicked() {
    if (!window.websocketManager.isConnected) {
      console.log('Disconnect called but you are not connected!');
      return;
    }
    await window.websocketManager.disconnect();
    this.connectBtn.prop('disabled', false);
    this.disconnectBtn.prop('disabled', true);
  }
}

class NewWorkingOrderTicketController {
  submitBtn = null;

  constructor(submitBtnSelector) {
    this.submitBtn = $(submitBtnSelector);
    this.submitBtn.on('click', this.handleNewOrderClicked.bind(this));
  }
  disableSubmitBtn() {
    this.submitBtn.prop('disabled', true);
  }
  enableSubmitBtn() {
    this.submitBtn.prop('disabled', false);
  }

  handleNewOrderClicked(e) {
    e.preventDefault();

    // In this example ClOrdId and TransactTime are populated by the consumer of this message
    const order = {
      msgType: 'D',
      symbol: $('#symbol').val(),
      qty: $('#qty').val(),
      px: $('#px').val(),
      clientID: $('#clientID').val(),
      side: $('#side-select').val(),
      ordType: $('#ord-type-select').val(),
      tif: $('#tif-select').val(),
    };

    window.workingOrdersManager.clientID = order.clientID;
    window.websocketManager.sendMessage('/app/new-order-single', order);
    window.messagesController.pushMessage('OUTBOUND', order);
  }
}

class CancelWorkingOrderTicketController {
  submitBtn = null;
  form = null;
  hideFormBtn = null;

  constructor(
    formSelector,
    cancelWorkingOrderBtnSelector,
    hideFormBtnSelector
  ) {
    this.form = $(formSelector);
    this.submitBtn = $(cancelWorkingOrderBtnSelector);
    this.hideFormBtn = $(hideFormBtnSelector);

    this.hideFormBtn.on('click', this.hide.bind(this));
    this.submitBtn.on('click', this.submitCancel.bind(this));
  }

  show() {
    this.form.prop('hidden', false);
  }

  hide() {
    this.form.prop('hidden', true);
  }

  newTicket(orderID) {
    const { clientID, workingOrders } = window.workingOrdersManager;
    const { symbol, clientOrderID, orderQty, side } = workingOrders[orderID];
    // hide the replace working order ticket if it is visible
    window.replaceWorkingOrderTicketController.hide();
    this.show();

    $('#orderCancelSymbol').val(symbol);
    $('#orderCancelClientID').val(clientID);
    $('#orderCancelQty').val(orderQty);
    $('#orderCancelOrigClOrdID').val(clientOrderID);
    $('#orderCancelSide-select').val(tradeSides[side]);
  }

  submitCancel(e) {
    e.preventDefault();

    const messageBody = {
      msgType: 'F',
      origClOrdID: $('#orderCancelOrigClOrdID').val(),
      clientID: $('#orderCancelClientID').val(),
      symbol: $('#orderCancelSymbol').val(),
      side: $('#orderCancelSide-select').val(),
      qty: $('#orderCancelQty').val(),
    };
    window.messagesController.pushMessage('OUTBOUND', messageBody);
    window.websocketManager.sendMessage('/app/order-cancel', messageBody);
  }
}

class ReplaceWorkingOrderTicketController {
  submitBtn = null;
  form = null;
  hideFormBtn = null;

  constructor(formSelector, submitBtnSelector, hideFormBtnSelector) {
    this.form = $(formSelector);
    this.submitBtn = $(submitBtnSelector);
    this.hideFormBtn = $(hideFormBtnSelector);

    this.hideFormBtn.on('click', this.hide.bind(this));
    this.submitBtn.on('click', this.submitReplace.bind(this));
  }

  show() {
    this.form.prop('hidden', false);
  }

  hide() {
    this.form.prop('hidden', true);
  }

  newTicket(orderID) {
    const { clientID, workingOrders } = window.workingOrdersManager;
    const {
      symbol,
      origClOrdID,
      orderQty,
      price,
      side,
      orderType,
      timeInForce,
    } = workingOrders[orderID];

    // hide the cancel working order ticket if it is visible
    window.cancelWorkingOrderTicketController.hide();
    this.show();

    $('#orderCancelReplaceRequestSymbol').val(symbol);
    $('#orderCancelReplaceRequestOrigClOrdID').val(origClOrdID);
    $('#orderCancelReplaceRequestQty').val(orderQty);
    $('#orderCancelReplaceRequestPx').val(price);
    $('#orderCancelReplaceRequestClientID').val(clientID);
    $('#orderCancelReplaceRequestSide-select').val(tradeSides[side]);
    $('#orderCancelReplaceRequestOrd-type-select').val(orderType);
    $('#orderCancelReplaceRequestTif-select').val(timeInForce);
  }

  submitReplace(e) {
    e.preventDefault();

    const messageBody = {
      msgType: 'G',
      origClOrdID: $('#orderCancelReplaceRequestOrigClOrdID').val(),
      clientID: $('#orderCancelReplaceRequestClientID').val(),
      symbol: $('#orderCancelReplaceRequestSymbol').val(),
      side: $('#orderCancelReplaceRequestSide-select').val(),
      qty: $('#orderCancelReplaceRequestQty').val(),
      ordType: $('#orderCancelReplaceRequestOrd-type-select').val(),
      px: $('#orderCancelReplaceRequestPx').val(), // price is required if a limit order
      tif: $('#orderCancelReplaceRequestTif-select').val(),
    };
    window.websocketManager.sendMessage(
      '/app/order-cancel-replace-request',
      messageBody
    );
    window.messagesController.pushMessage('OUTBOUND', messageBody);
  }
}

// ************* Utils ************* //
function workingOrderRowId(id) {
  return `wo-${id}`;
}

const tradeSides = {
  1: 'BUY',
  2: 'SELL',
};
// ********************************* //

document.addEventListener('DOMContentLoaded', () => {
  window.websocketManager = new WebSocketManager();
  window.workingOrdersManager = new WorkingOrdersManager();

  // UI Controllers
  window.wsControlsController = new WSControlsController(
    '#connect',
    '#disconnect',
    '#socket-status'
  );
  window.messagesController = new MessagesListController('#messages');
  window.workingOrdersTableController = new WorkingOrdersTableController(
    '#working-orders'
  );
  window.newWorkingOrderTicketController = new NewWorkingOrderTicketController(
    '#send-new-order'
  );
  window.cancelWorkingOrderTicketController =
    new CancelWorkingOrderTicketController(
      '#cancel-order-ticket',
      '#sendOrderCancel',
      '#hide-cancel-order-ticket'
    );
  window.replaceWorkingOrderTicketController =
    new ReplaceWorkingOrderTicketController(
      '#replace-order-ticket',
      '#sendOrderCancelReplaceRequest',
      '#hide-replace-order-ticket'
    );
});

const exampleMsg = {
  avgPx: '0',
  clientOrderID: '5bc3eba7-5489-4e10-82ec-e395723f61a2',
  cumQty: '0',
  execID: '202510221504ONRP00000022E',
  execType: null,
  lastPx: null,
  lastQty: null,
  leavesQty: '10',
  msgType: '8',
  ordRejReason: null,
  ordStatus: '0',
  orderID: '202510221504VWLR00000018O',
  orderQty: '10',
  price: '0.1',
  side: '1',
  symbol: 'NX.F.OPT.NFL-WN681370.O.1.16',
  text: null,
  timeInForce: null,
  tradeDate: null,
  transactTime: '2025-10-22T14:04:16.852',
};

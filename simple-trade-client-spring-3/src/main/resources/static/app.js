/* Simple Working Order submission example

    Developer note:
        - uses JQuery - all the DOM manipulation methods you see such as $() are not native browser APIs.
        - uses tailwindCSS for styling. This provides composable helper classes which you add to elements to style them.
*/

class OrderEntryWebsocketManager {
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
    if (parsedMessage.msgType === 'AP') {
      this._handlePositionReport(parsedMessage);
    }
  }

  _handleExecutionReport(message) {
    if (message.execType !== 'I') {
      if (message.ordStatus === '0') {
        if (message.execType === '0') {
          window.workingOrdersManager.newOrder(message);
        } else if (message.execType === '4') {
          window.workingOrdersManager.orderCancelled(message);
        } else if (message.execType === '5') {
          window.workingOrdersManager.orderReplaced(message);
        }
        // Order Filled
      } else if (message.ordStatus === '2') {
        window.workingOrdersManager.orderFilled(message);
      } else if (message.ordStatus === '1') {
        window.workingOrdersManager.partialFill(message); // TODO:
      }
    }
  }

  _handlePositionReport(message) {
    const { symbol, positionQtys, settlPrice } = message;

    if (positionQtys.length === 1) {
      const { longQty, shortQty, posQtyStatus } = positionQtys[0];

      if (posQtyStatus === 1) {
        const netPosition = shortQty + longQty;

        window.positionsManager.handlePositionUpdate(
          symbol,
          settlPrice,
          netPosition
        );
      } else {
        console.error('Position was not accepted.');
      }
    } else {
      console.error(`Position Report has ${positionQtys.length} positions. This scenario has not been implemented`);
    }
  }
}

class MarketDataWebsocketManager {
  client = null;
  isConnected = false;
  socketState = 'INACTIVE';

  constructor() {
    this.client = new StompJs.Client({
      brokerURL: 'ws://localhost:8081/market-data-websocket',
    });

    this.client.onConnect = (frame) => {
      this.isConnected = true;
      console.log('Connected: ' + frame);
      this.client.subscribe('/topic/messages', this._handleMessage.bind(this));
    };

    this.client.onDisconnect = () => {
      this.isConnected = false;
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
    console.log(message);
  }
}

class WorkingOrdersManager {
  constructor() {
    this.clientID = null;
    this.workingOrders = {};
  }

  newOrder(order) {
    this.workingOrders[order.clientOrderID] = order;
    window.workingOrdersTableController.addRow(order);
  }
  orderFilled(order) {
    window.workingOrdersTableController.removeRow(order.clientOrderID);
    delete this.workingOrders[order.clientOrderID];
  }
  partialFill(order) {
    console.error('Partial fill not implemented!');
  }
  orderCancelled(order) {
    window.workingOrdersTableController.removeRow(order.clientOrderID);
    delete this.workingOrders[order.clientOrderID];
  }
  orderReplaced(order) {
    delete this.workingOrders[order.originalClientOrderID];
    this.workingOrders[order.clientOrderID] = order;

    window.workingOrdersTableController.replaceRow(
      order.originalClientOrderID,
      order
    );
  }
}

class WorkingOrdersTableController {
  workingOrdersTable = null;

  constructor(workingOrdersTableSelector) {
    this.workingOrdersTable = $(workingOrdersTableSelector);
  }

  addRow({
    transactTime,
    symbol,
    orderID,
    orderQty,
    price,
    clientOrderID,
    side,
    timeInForce,
    orderType, // TODO: this is not present on message
  }) {
    const fields = [
      transactTime,
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
				onclick="window.cancelWorkingOrderTicketController.newTicket.call(window.cancelWorkingOrderTicketController, '${clientOrderID}')"
				>Cancel</button>
		</td>`);
    cells.push(`<td class='text-[#00aeef] font-bold px-2 py-1 text-left text-xs'>
			<button
				class='cursor-pointer p-1'
				onclick="window.replaceWorkingOrderTicketController.newTicket.call(window.replaceWorkingOrderTicketController, '${clientOrderID}')"
				>Edit</button
		</td>`);
    const rowHtml = `<tr id=${workingOrderRowId(clientOrderID)}>${cells}</tr>`;
    this.workingOrdersTable.append(rowHtml);
  }

  removeRow(clientOrderId) {
    const tableRowId = workingOrderRowId(clientOrderId);
    // I can't get jquery's .remove() to work so using native here
    document.querySelector(`#${tableRowId}`).remove();
  }

  replaceRow(existingRowId, newOrder) {
    this.removeRow(existingRowId);
    this.addRow(newOrder);
  }
}

class PositionsManager {
  positions = {};

  handlePositionUpdate(symbol, averagePrice, quantity) {
    this.positions[symbol] = {
      symbol,
      averagePrice,
      quantity,
      lastUpdated: new Date(),
    };
    window.positionsTableController.addRow(this.positions[symbol]);
  }
}

class PositionsTableController {
  positionsTable = null;

  constructor(positionsTableSelector) {
    this.positionsTable = $(positionsTableSelector);
  }

  addRow({ symbol, quantity, averagePrice, lastUpdated }) {
    const qtyClass =
      parseFloat(quantity) >= 0 ? 'text-green-500' : 'text-red-500';
    const qtyPrefix = parseFloat(quantity) >= 0 ? '+' : '';
    const html = `
      <tr id="${getPositionRowId(symbol)}">
        <td class='px-1 text-center text-xs overflow-hidden truncate' title="${symbol}">${symbol}</td>
        <td class='px-1 text-center text-xs overflow-hidden truncate ${qtyClass}' title="${qtyPrefix}${quantity}">${qtyPrefix}${quantity}</td>
        <td class='px-1 text-center text-xs overflow-hidden truncate' title="${averagePrice}">${averagePrice.toFixed(
      2
    )}</td>
        <td class='px-1 text-center text-xs overflow-hidden truncate' title="${lastUpdated.toLocaleTimeString()}">${lastUpdated.toLocaleTimeString()}</td>
      </tr>
      `;
    this.positionsTable.append(html);
  }

  removeRow(symbol) {
    const tableRowId = getPositionRowId(symbol);
    // I can't get jquery's .remove() to work so using native here
    document.querySelector(`#${tableRowId}`).remove();
  }

  replaceRow(existingSymbol, newPosition) {
    this.removeRow(existingSymbol);
    this.addRow(newPosition);
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
    window.orderEntryWebsocketManager.connect();
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
    if (!window.orderEntryWebsocketManager.isConnected) {
      console.log('Disconnect called but you are not connected!');
      return;
    }
    await window.orderEntryWebsocketManager.disconnect();
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
      px: `${$('#px').val()}`, // stringify this numerical input
      clientID: $('#clientID').val(),
      side: $('#side-select').val(),
      ordType: $('#ord-type-select').val(),
      tif: $('#tif-select').val(),
    };

    window.workingOrdersManager.clientID = order.clientID;
    window.orderEntryWebsocketManager.sendMessage(
      '/app/new-order-single',
      order
    );
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
    window.orderEntryWebsocketManager.sendMessage(
      '/app/order-cancel',
      messageBody
    );
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
      clientOrderID,
      orderQty,
      price,
      side,
      orderType, // TODO: this is not returned on FIX working order accepted message
      timeInForce,
    } = workingOrders[orderID];

    // hide the cancel working order ticket if it is visible
    window.cancelWorkingOrderTicketController.hide();
    this.show();

    $('#orderCancelReplaceRequestSymbol').val(symbol);
    $('#orderCancelReplaceRequestOrigClOrdID').val(clientOrderID);
    $('#orderCancelReplaceRequestQty').val(orderQty);
    $('#orderCancelReplaceRequestPx').val(price);
    $('#orderCancelReplaceRequestClientID').val(clientID);
    $('#orderCancelReplaceRequestSide-select').val(tradeSides[side]);
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
    window.orderEntryWebsocketManager.sendMessage(
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

// symbol contains '.' characters which break css selectors
function getPositionRowId(symbol) {
  const sanitisedSymbold = symbol.replace(/[.:]/g, '_');
  return `pos-${sanitisedSymbold}`;
}

const tradeSides = {
  1: 'BUY',
  2: 'SELL',
};

function getAveragePrice(existingAvgPrice, existingQty, fillPrice, fillQty) {
  const totalCostExisting = existingAvgPrice * existingQty;
  const totalCostNew = fillPrice * fillQty;
  const newTotalQty = existingQty + fillQty;
  return (totalCostExisting + totalCostNew) / newTotalQty;
}

// ********************************* //

document.addEventListener('DOMContentLoaded', () => {
  window.orderEntryWebsocketManager = new OrderEntryWebsocketManager();

  window.wsControlsController = new WSControlsController(
    '#connect',
    '#disconnect',
    '#socket-status'
  );
  window.messagesController = new MessagesListController('#messages');

  // Working Orders
  window.workingOrdersManager = new WorkingOrdersManager();
  window.workingOrdersTableController = new WorkingOrdersTableController(
    '#working-orders-table-body'
  );

  // Positions
  window.positionsManager = new PositionsManager();
  window.positionsTableController = new PositionsTableController(
    '#positions-table-body'
  );

  // Tickets
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

// Example new order Execution Report
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

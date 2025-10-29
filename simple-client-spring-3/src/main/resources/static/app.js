/* Simple Working Order submission example

    Developer note:
        - uses JQuery - all the DOM manipulation methods you see such as $() are not native browser APIs.
        - uses tailwindCSS for styling. This provides composable helper classes which you add to elements to style them.
*/

class MarketDataWebsocketManager {
  client = null;
  isConnected = false;
  socketState = 'INACTIVE';

  constructor() {
    this.client = new StompJs.Client({
      brokerURL: 'ws://localhost:8081/pre-trade-websocket',
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
    const parsed = JSON.parse(message.body);
    if (parsed.msgType === 'W') {
      window.marketDataManager.handleMarketDataUpdate(parsed);
    }
  }
}

class MarketDataManager {
  markets = new Map();
  constructor() {}

  handleMarketDataUpdate(update) {
    this.markets.set(update.symbol, update);
  }
}

class MarketListTableController {
  marketListTable = null;
  numberOfMarkets = null;
  constructor(
    marketListTableSelector,
    numberOfMarketsSelector,
    updateInterval
  ) {
    this.marketListTable = $(marketListTableSelector);
    this.numberOfMarkets = $(numberOfMarketsSelector);

    setInterval(() => {
      if (window.marketDataWebsocketManager.isConnected) {
        this.updateMarketList();
        // console.log(`markets: ${window.marketDataManager.markets}`);
      }
    }, updateInterval);
  }

  updateMarketList() {
    let i = 0;
    const numberOfMarkets = window.marketDataManager.markets.size;
    const arr = new Array(numberOfMarkets);
    window.marketDataManager.markets.forEach((value) => {
      arr[i] = `<tr>
        <td class='text-center'>${value.symbol}</td>
        <td class='text-center text-green-500'>${value.bid.mdEntryPx}</td>
        <td class='text-center text-green-500'>${value.bid.mdEntrySize}</td>
        <td class='text-center text-red-500'>${value.offer.mdEntryPx}</td>
        <td class='text-center text-red-500'>${value.offer.mdEntrySize}</td>
        <td class='text-center'>${value.marketDepth}</td>
      </tr>`;
      i++;
    });
    this.numberOfMarkets.html(`(${numberOfMarkets})`);
    this.marketListTable.html(arr.join());
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
    window.marketDataWebsocketManager.connect();
    this.setSocketStatusIndicator('ACTIVE');
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
    if (!window.marketDataWebsocketManager.isConnected) {
      console.log('Disconnect called but you are not connected!');
      return;
    }
    await window.marketDataWebsocketManager.disconnect();
    this.connectBtn.prop('disabled', false);
    this.disconnectBtn.prop('disabled', true);
    this.setSocketStatusIndicator('INACTIVE');
  }
}

document.addEventListener('DOMContentLoaded', () => {
  window.marketDataWebsocketManager = new MarketDataWebsocketManager();
  window.wsControlsController = new WSControlsController(
    '#connect',
    '#disconnect',
    '#socket-status'
  );
  window.marketDataManager = new MarketDataManager();
  window.marketListTableController = new MarketListTableController(
    '#market-list-table-body',
    '#number-of-markets',
    1000
  );
});

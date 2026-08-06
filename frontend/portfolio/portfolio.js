const API_BASE_URL = resolveApiBaseUrl();

function resolveApiBaseUrl() {
	const configuredBase = window.localStorage.getItem('apiBaseUrl');
	const normalizedConfiguredBase =
		configuredBase && configuredBase.trim()
			? configuredBase.trim().replace(/\/$/, '')
			: '';

	if (window.location.protocol === 'file:') {
		return normalizedConfiguredBase || 'http://localhost:8090';
	}

	// In deployed mode, prefer the reverse-proxy path and avoid stale localhost overrides.
	if (normalizedConfiguredBase) {
		if (normalizedConfiguredBase.startsWith(window.location.origin)) {
			return normalizedConfiguredBase;
		}
		if (normalizedConfiguredBase.startsWith('/')) {
			return normalizedConfiguredBase.replace(/\/$/, '');
		}
	}

	return `${window.location.origin}/api`;
}

const ASSET_TYPES = ['stocks', 'bonds', 'mutual funds'];
const ASSET_TYPE_LABELS = {
	stocks: 'Stocks',
	bonds: 'Bonds',
	'mutual funds': 'Mutual Funds',
	cash: 'Cash'
};
function showToast(message, type = "success") {

	const toast = document.getElementById("toast");

	if (!toast) return;

	toast.textContent = message;

	toast.className = `toast show ${type}`;

	setTimeout(() => {
		toast.className = "toast";
	}, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
	const pageTitle = document.getElementById('pageTitle');
	const portfolioDescription = document.getElementById('portfolioDescription');
	const investorDetails = document.getElementById('investorDetails');

	const stocksValue = document.getElementById('stocksValue');
	const bondsValue = document.getElementById('bondsValue');
	const mutualFundsValue = document.getElementById('mutualFundsValue');
	const totalFundsValue = document.getElementById('totalFundsValue');
	const usedFundsValue = document.getElementById('usedFundsValue');
	const availableFundsValue = document.getElementById('availableFundsValue');
	const returnPercentValue = document.getElementById('returnPercentValue');
	const gainLossValue = document.getElementById('gainLossValue');
	const gainLossLabel = document.getElementById('gainLossLabel');

	const allocationChartCanvas = document.getElementById('allocationChart');
	const allocationEmptyState = document.getElementById('allocationEmptyState');
	const availableFundsAllocationValue = document.getElementById('availableFundsAllocationValue');

	const performanceTabs = document.getElementById('performanceTabs');
	const performanceSubLabel = document.getElementById('performanceSubLabel');
	const performanceChartCanvas = document.getElementById('performanceChart');
	const performanceEmptyState = document.getElementById('performanceEmptyState');
	const performanceTable = document.getElementById('performanceTable');

	const totalFundsReceivedValue = document.getElementById('totalFundsReceivedValue');
	const addFundsBtn = document.getElementById('addFundsBtn');
	const addFundsModal = document.getElementById('addFundsModal');
	const closeAddFundsModalBtn = document.getElementById('closeAddFundsModalBtn');
	const cancelAddFundsBtn = document.getElementById('cancelAddFundsBtn');
	const addFundsForm = document.getElementById('addFundsForm');
	const fundNameInput = document.getElementById('fundNameInput');
	const fundAmountInput = document.getElementById('fundAmountInput');
	const fundDateInput = document.getElementById('fundDateInput');
	const fundStatusInput = document.getElementById('fundStatusInput');
	const addFundsMessage = document.getElementById('addFundsMessage');
	const submitAddFundsBtn = document.getElementById('submitAddFundsBtn');

	const investmentsByType = document.getElementById('investmentsByType');
	const emptyState = document.getElementById('emptyState');
	const buyBtn = document.getElementById('buyBtn');

	const viewModal = document.getElementById('viewInvestmentModal');
	const viewModalBody = document.getElementById('viewModalBody');
	const closeViewModalBtn = document.getElementById('closeViewModalBtn');

	const formModal = document.getElementById('investmentFormModal');
	const closeFormModalBtn = document.getElementById('closeFormModalBtn');
	const cancelFormBtn = document.getElementById('cancelFormBtn');
	const investmentForm = document.getElementById('investmentForm');
	const formInvestmentIdInput = document.getElementById('formInvestmentId');
	const formAssetIdInput = document.getElementById('formAssetId');
	const formPurchasePriceInput = document.getElementById('formPurchasePrice');
	const formQuantityInput = document.getElementById('formQuantity');
	const formPurchaseDateInput = document.getElementById('formPurchaseDate');
	const formLiveMarketPrice = document.getElementById('formLiveMarketPrice');
	const formModalTitle = document.getElementById('formModalTitle');
	const submitFormBtn = document.getElementById('submitFormBtn');
	const formMessage = document.getElementById('formMessage');

	const tradeModal = document.getElementById('tradeModal');
	const tradeModalTitle = document.getElementById('tradeModalTitle');
	const closeTradeModalBtn = document.getElementById('closeTradeModalBtn');
	const cancelTradeBtn = document.getElementById('cancelTradeBtn');
	const tradeForm = document.getElementById('tradeForm');
	const tradeModeInput = document.getElementById('tradeMode');
	const tradeInvestmentIdInput = document.getElementById('tradeInvestmentId');
	const tradeAssetIdInput = document.getElementById('tradeAssetId');
	const tradeHoldingInfo = document.getElementById('tradeHoldingInfo');
	const tradeLivePrice = document.getElementById('tradeLivePrice');
	const tradeQuantityInput = document.getElementById('tradeQuantity');
	const tradeEstimate = document.getElementById('tradeEstimate');
	const tradeMessage = document.getElementById('tradeMessage');
	const submitTradeBtn = document.getElementById('submitTradeBtn');

	const transactionsModal = document.getElementById('transactionsModal');
	const transactionsModalBody = document.getElementById('transactionsModalBody');
	const closeTransactionsModalBtn = document.getElementById('closeTransactionsModalBtn');

	if (
		!pageTitle ||
		!portfolioDescription ||
		!investorDetails ||
		!stocksValue ||
		!bondsValue ||
		!mutualFundsValue ||
		!totalFundsValue ||
		!usedFundsValue ||
		!availableFundsValue ||
		!returnPercentValue ||
		!gainLossValue ||
		!gainLossLabel ||
		!totalFundsReceivedValue ||
		!addFundsBtn ||
		!addFundsModal ||
		!closeAddFundsModalBtn ||
		!cancelAddFundsBtn ||
		!addFundsForm ||
		!fundNameInput ||
		!fundAmountInput ||
		!fundDateInput ||
		!fundStatusInput ||
		!addFundsMessage ||
		!submitAddFundsBtn ||
		!investmentsByType ||
		!emptyState ||
		!viewModal ||
		!viewModalBody ||
		!closeViewModalBtn ||
		!formModal ||
		!closeFormModalBtn ||
		!cancelFormBtn ||
		!investmentForm ||
		!formInvestmentIdInput ||
		!formAssetIdInput ||
		!formPurchasePriceInput ||
		!formQuantityInput ||
		!formPurchaseDateInput ||
		!formLiveMarketPrice ||
		!formModalTitle ||
		!submitFormBtn ||
		!formMessage ||
		!buyBtn ||
		!tradeModal ||
		!tradeModalTitle ||
		!closeTradeModalBtn ||
		!cancelTradeBtn ||
		!tradeForm ||
		!tradeModeInput ||
		!tradeInvestmentIdInput ||
		!tradeAssetIdInput ||
		!tradeHoldingInfo ||
		!tradeLivePrice ||
		!tradeQuantityInput ||
		!tradeEstimate ||
		!tradeMessage ||
		!submitTradeBtn ||
		!transactionsModal ||
		!transactionsModalBody ||
		!closeTransactionsModalBtn ||
		!allocationChartCanvas ||
		!allocationEmptyState ||
		!availableFundsAllocationValue ||
		!performanceTabs ||
		!performanceSubLabel ||
		!performanceChartCanvas ||
		!performanceEmptyState ||
		!performanceTable
	) {
		return;
	}

	const portfolioId = getPortfolioId();
	let investorId = null;
	let investments = [];
	let assets = [];
	let summary = null;
	let performanceData = null;
	let activePerformancePeriod = 'monthly';
	let allocationChartInstance = null;
	let performanceChartInstance = null;
	const livePriceCache = new Map();

	formPurchaseDateInput.max = getTodayIsoDate();
	initializePage();

	formAssetIdInput.addEventListener('change', async () => {
		await loadCurrentMarketPriceForSelectedAsset();
	});
	closeViewModalBtn.addEventListener('click', closeViewModal);
	closeFormModalBtn.addEventListener('click', closeFormModal);
	cancelFormBtn.addEventListener('click', closeFormModal);

	buyBtn.addEventListener('click', () => openTradeModal('buy'));
	closeTradeModalBtn.addEventListener('click', closeTradeModal);
	cancelTradeBtn.addEventListener('click', closeTradeModal);
	closeTransactionsModalBtn.addEventListener('click', closeTransactionsModal);

	addFundsBtn.addEventListener('click', openAddFundsModal);
	closeAddFundsModalBtn.addEventListener('click', closeAddFundsModal);
	cancelAddFundsBtn.addEventListener('click', closeAddFundsModal);

	tradeAssetIdInput.addEventListener('change', async () => {
		await loadCurrentMarketPriceForTrade();
	});
	tradeQuantityInput.addEventListener('input', updateTradeEstimate);

	viewModal.addEventListener('click', (event) => {
		if (event.target === viewModal) {
			closeViewModal();
		}
	});

	formModal.addEventListener('click', (event) => {
		if (event.target === formModal) {
			closeFormModal();
		}
	});

	tradeModal.addEventListener('click', (event) => {
		if (event.target === tradeModal) {
			closeTradeModal();
		}
	});

	transactionsModal.addEventListener('click', (event) => {
		if (event.target === transactionsModal) {
			closeTransactionsModal();
		}
	});

	addFundsModal.addEventListener('click', (event) => {
		if (event.target === addFundsModal) {
			closeAddFundsModal();
		}
	});

	document.addEventListener('keydown', (event) => {
		if (event.key === 'Escape') {
			closeViewModal();
			closeFormModal();
			closeTradeModal();
			closeTransactionsModal();
			closeAddFundsModal();
		}
	});

	investmentsByType.addEventListener('click', async (event) => {
		const target = event.target;
		if (!(target instanceof HTMLElement)) {
			return;
		}

		const investmentIdRaw = target.getAttribute('data-id');
		if (!investmentIdRaw) {
			return;
		}

		const investmentId = Number(investmentIdRaw);
		if (!Number.isFinite(investmentId)) {
			return;
		}

		if (target.classList.contains('view-btn')) {
			await handleViewInvestment(investmentId);
			return;
		}

		if (target.classList.contains('sell-btn')) {
			const investment = investments.find((item) => item.investmentId === investmentId);
			if (investment) {
				openTradeModal('sell', investment);
			}
			return;
		}

		if (target.classList.contains('transactions-btn')) {
			await handleShowTransactions(investmentId);
		}
	});

	investmentForm.addEventListener('submit', async (event) => {
		event.preventDefault();
		clearFormMessage();

		const investmentId = Number(formInvestmentIdInput.value);
		const payload = {
			assetId: Number(formAssetIdInput.value),
			purchasePrice: Number(formPurchasePriceInput.value),
			quantity: Number(formQuantityInput.value),
			purchaseDate: formPurchaseDateInput.value
		};

		const validationMessage = validateFormPayload(payload, investmentId);
		if (validationMessage) {
			setFormMessage(validationMessage, true);
			return;
		}

		submitFormBtn.disabled = true;

		try {
			await updateInvestment(investmentId, payload);

			closeFormModal();
			await Promise.all([loadInvestments(), loadPortfolioSummary()]);
		} catch (error) {
			setFormMessage(error instanceof Error ? error.message : 'Unable to save investment.', true);
		} finally {
			submitFormBtn.disabled = false;
		}
	});

	tradeForm.addEventListener('submit', async (event) => {
		event.preventDefault();
		clearTradeMessage();

		const mode = tradeModeInput.value;
		const payload = {
			assetId: Number(tradeAssetIdInput.value),
			quantity: Number(tradeQuantityInput.value)
		};

		const validationMessage = validateTradePayload(payload, mode);
		if (validationMessage) {
			setTradeMessage(validationMessage, true);
			return;
		}

		submitTradeBtn.disabled = true;

		try {
			if (mode === 'buy') {
				await buyInvestment(payload);
			} else {
				await sellInvestment(payload);
			}

			closeTradeModal();
			await Promise.all([loadInvestments(), loadPortfolioSummary()]);
			showToast(mode === 'buy' ? 'Purchase successful!' : 'Sale successful!', 'success');
		} catch (error) {
			setTradeMessage(error instanceof Error ? error.message : `Unable to ${mode} investment.`, true);
			showToast(mode === 'buy' ? 'Failed to buy investment.' : 'Failed to sell investment.', 'error');
		} finally {
			submitTradeBtn.disabled = false;
		}
	});

	addFundsForm.addEventListener('submit', async (event) => {
		event.preventDefault();
		clearAddFundsMessage();

		const payload = {
			fundId: 0,
			investorId,
			fundName: fundNameInput.value.trim(),
			amountReceived: Number(fundAmountInput.value),
			receivedDate: fundDateInput.value,
			status: fundStatusInput.value
		};

		if (!payload.fundName) {
			setAddFundsMessage('Please enter a fund name.', true);
			return;
		}

		if (!Number.isFinite(payload.amountReceived) || payload.amountReceived <= 0) {
			setAddFundsMessage('Please enter a valid amount.', true);
			return;
		}

		if (!payload.receivedDate) {
			setAddFundsMessage('Please choose a received date.', true);
			return;
		}

		submitAddFundsBtn.disabled = true;

		try {
			await fetchJson(`${API_BASE_URL}/funds/investor/${encodeURIComponent(investorId)}`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			});

			closeAddFundsModal();
			await Promise.all([loadInvestorFunds(), loadPortfolioSummary()]);
			showToast('Funds added successfully!', 'success');
		} catch (error) {
			setAddFundsMessage(error instanceof Error ? error.message : 'Unable to add funds.', true);
			showToast('Failed to add funds.', 'error');
		} finally {
			submitAddFundsBtn.disabled = false;
		}
	});

	async function initializePage() {
		renderInvestorLoading();
		renderSummaryLoading();
		renderPerformanceLoading();
		await Promise.all([
			loadAssets(),
			loadPortfolioAndInvestor(),
			loadPortfolioSummary(),
			loadInvestments(),
			loadPortfolioPerformance()
		]);
	}

	performanceTabs.addEventListener('click', (event) => {
		const target = event.target;
		if (!(target instanceof HTMLElement) || !target.classList.contains('tab-btn')) {
			return;
		}

		const period = target.getAttribute('data-period');
		if (!period || period === activePerformancePeriod) {
			return;
		}

		activePerformancePeriod = period;

		performanceTabs.querySelectorAll('.tab-btn').forEach((btn) => {
			const isActive = btn === target;
			btn.classList.toggle('active', isActive);
			btn.setAttribute('aria-selected', String(isActive));
		});

		renderPerformance();
	});

	async function loadAssets() {
		try {
			const data = await fetchJson(`${API_BASE_URL}/assets`);
			assets = Array.isArray(data) ? data : [];
			renderAssetOptions();
		} catch (error) {
			assets = [];
			renderAssetOptions();
		}
	}

	function renderAssetOptions() {
		formAssetIdInput.innerHTML = '<option value="">Select an asset</option>';
		tradeAssetIdInput.innerHTML = '<option value="">Select an asset</option>';

		assets.forEach((asset) => {
			const assetId = Number(asset.assetId ?? asset.asset_id ?? 0);
			if (!Number.isFinite(assetId) || assetId <= 0) {
				return;
			}

			const normalizedType = normalizeAssetType(asset.assetType ?? asset.asset_type);
			if (normalizedType === 'cash') {
				return;
			}

			const ticker = firstNonEmpty(asset.tickerSymbol, asset.ticker_symbol);
			const assetName = firstNonEmpty(asset.assetName, asset.asset_name);
			const typeLabel = normalizedType ? ASSET_TYPE_LABELS[normalizedType] : 'Unknown';
			const optionLabel = `${ticker} - ${assetName} (${typeLabel})`;

			const option = document.createElement('option');
			option.value = String(assetId);
			option.textContent = optionLabel;
			formAssetIdInput.appendChild(option);

			const tradeOption = document.createElement('option');
			tradeOption.value = String(assetId);
			tradeOption.textContent = optionLabel;
			tradeAssetIdInput.appendChild(tradeOption);
		});
	}

	async function loadPortfolioAndInvestor() {
		try {
			const portfolio = await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}`);
			pageTitle.textContent = portfolio.portfolioName || `Portfolio ${portfolioId}`;
			portfolioDescription.textContent = portfolio.portfolioDescription || 'No description available.';
		} catch (error) {
			pageTitle.textContent = `Portfolio ${portfolioId}`;
			portfolioDescription.textContent = 'Unable to load portfolio description.';
		}

		try {
			const investorIdPayload = await fetchJson(
				`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investor-id`
			);
			investorId = investorIdPayload.investorId;
			const investor = await fetchJson(`${API_BASE_URL}/investors/${encodeURIComponent(investorId)}`);
			renderInvestor(investor);
			await loadInvestorFunds();
		} catch (error) {
			renderInvestorError(error instanceof Error ? error.message : 'Failed to load investor details.');
		}
	}

	async function loadInvestorFunds() {
		if (!investorId) {
			totalFundsReceivedValue.textContent = '-';
			return;
		}

		totalFundsReceivedValue.textContent = 'Loading...';

		try {
			const data = await fetchJson(
				`${API_BASE_URL}/funds/investor/${encodeURIComponent(investorId)}/total`
			);
			totalFundsReceivedValue.textContent = formatCurrency(Number(data.totalFundsReceived ?? 0));
		} catch (error) {
			totalFundsReceivedValue.textContent = '-';
		}
	}

	async function loadPortfolioSummary() {
		try {
			const data = await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/summary`);
			summary = data;
			renderSummary(data);
			renderInvestments();
			renderAllocationChart(data);
		} catch (error) {
			summary = null;
			renderSummaryError(error instanceof Error ? error.message : 'Failed to load portfolio summary.');
			renderAllocationChart(null);
		}
	}

	async function loadPortfolioPerformance() {
		try {
			const data = await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/performance`);
			performanceData = data;
			renderPerformance();
		} catch (error) {
			performanceData = null;
			renderPerformanceError(error instanceof Error ? error.message : 'Failed to load portfolio performance.');
		}
	}

	async function loadInvestments() {
		try {
			const data = await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments`);
			investments = Array.isArray(data) ? data.map(normalizeInvestment) : [];
			renderInvestments();
		} catch (error) {
			investments = [];
			renderInvestments();
			emptyState.hidden = false;
			emptyState.textContent = error instanceof Error
				? error.message
				: 'Unable to load investments.';
		}
	}

	function renderInvestments() {
		investmentsByType.innerHTML = '';
		emptyState.textContent = 'No investments in this portfolio.';

		const visibleInvestments = investments.filter(
			(investment) => Number(investment.quantity) > 0
		);

		if (visibleInvestments.length === 0) {
			emptyState.hidden = false;
		} else {
			emptyState.hidden = true;
		}

		const grouped = groupInvestmentsByType(visibleInvestments);

		ASSET_TYPES.forEach((typeKey) => {
			const section = document.createElement('section');
			section.className = 'asset-group';

			const header = document.createElement('div');
			header.className = 'asset-group-header';
			header.innerHTML = `
				<h3 class="asset-group-title">${escapeHtml(ASSET_TYPE_LABELS[typeKey])}</h3>
			`;

			const itemsContainer = document.createElement('div');
			itemsContainer.className = 'asset-items';

			if (grouped[typeKey].length === 0) {
				itemsContainer.innerHTML = '<p class="empty-state">No investments in this asset type.</p>';
			} else {
				grouped[typeKey].forEach((investment) => {
					const row = document.createElement('article');
					row.className = 'asset-item';

					const livePrice = getLivePriceFromInvestment(investment);

					row.innerHTML = `
						<div class="asset-item-main">
							<div class="asset-item-line"><strong>${escapeHtml(investment.tickerSymbol || '-')}</strong></div>
							<div class="asset-item-line">Quantity Held: ${formatNumber(investment.quantity)}</div>
							<div class="asset-item-line">Holdings: ${formatCurrency(investment.currentValue)}</div>
						</div>
						<div class="actions">
							<button type="button" class="secondary-btn view-btn" data-id="${investment.investmentId}">View</button>
							<button type="button" class="secondary-btn sell-btn" data-id="${investment.investmentId}">Sell</button>
							<button type="button" class="secondary-btn transactions-btn" data-id="${investment.investmentId}">Transactions</button>
						</div>
					`;
					itemsContainer.appendChild(row);
				});
			}

			section.appendChild(header);
			section.appendChild(itemsContainer);
			investmentsByType.appendChild(section);
		});
	}

	function groupInvestmentsByType(items) {
		const grouped = {
			stocks: [],
			bonds: [],
			'mutual funds': []
		};

		items.forEach((item) => {
			const typeKey = normalizeAssetType(item.assetType);
			if (typeKey && grouped[typeKey]) {
				grouped[typeKey].push(item);
			}
		});

		return grouped;
	}

	function renderInvestorLoading() {
		investorDetails.innerHTML = buildSingleMessageCard('Investor Details', 'Loading...');
	}

	function renderInvestor(investor) {
		const fields = [
			{ label: 'Investor ID', value: investor.investorId },
			{ label: 'Investor Name', value: investor.investorName },
			{ label: 'Email', value: investor.investorEmail }
		];

		investorDetails.innerHTML = fields
			.map(
				(field) => `
					<div class="investor-item">
						<div class="investor-item-label">${escapeHtml(field.label)}</div>
						<div class="investor-item-value">${escapeHtml(String(field.value ?? '-'))}</div>
					</div>
				`
			)
			.join('');
	}

	function renderInvestorError(message) {
		investorDetails.innerHTML = buildSingleMessageCard('Investor Details', message);
	}

	function renderSummaryLoading() {
		stocksValue.textContent = 'Loading...';
		bondsValue.textContent = 'Loading...';
		mutualFundsValue.textContent = 'Loading...';
		totalFundsValue.textContent = 'Loading...';
		usedFundsValue.textContent = 'Loading...';
		availableFundsValue.textContent = 'Loading...';
		returnPercentValue.textContent = 'Loading...';
		gainLossValue.textContent = 'Loading...';
		gainLossLabel.textContent = 'Loading...';
		setGainLossClass('Neutral');
	}

	function renderSummary(data) {
		stocksValue.textContent = formatCurrency(Number(data.stocksInvested ?? 0));
		bondsValue.textContent = formatCurrency(Number(data.bondsInvested ?? 0));
		mutualFundsValue.textContent = formatCurrency(Number(data.mutualFundsInvested ?? 0));
		totalFundsValue.textContent = formatCurrency(Number(data.totalFunds ?? 0));
		usedFundsValue.textContent = formatCurrency(Number(data.usedFunds ?? 0));
		availableFundsValue.textContent = formatCurrency(Number(data.availableFunds ?? 0));
		returnPercentValue.textContent = `${formatPercent(Number(data.returnPercent ?? 0))}%`;

		const gainLoss = Number(data.gainLoss ?? 0);
		const label = typeof data.gainLossLabel === 'string' ? data.gainLossLabel : 'Neutral';
		gainLossValue.textContent = formatCurrency(gainLoss);
		gainLossLabel.textContent = label;
		setGainLossClass(label);
	}

	function renderSummaryError(message) {
		stocksValue.textContent = '-';
		bondsValue.textContent = '-';
		mutualFundsValue.textContent = '-';
		totalFundsValue.textContent = '-';
		usedFundsValue.textContent = '-';
		availableFundsValue.textContent = '-';
		returnPercentValue.textContent = '-';
		gainLossValue.textContent = '-';
		gainLossLabel.textContent = message;
		setGainLossClass('Neutral');
	}

	function renderAllocationChart(data) {
		const stocks = data ? Number(data.stocksInvested ?? 0) : 0;
		const bonds = data ? Number(data.bondsInvested ?? 0) : 0;
		const mutualFunds = data ? Number(data.mutualFundsInvested ?? 0) : 0;
		const cash = data ? Number(data.cashInvested ?? 0) : 0;
		const available = data ? Number(data.availableFunds ?? 0) : 0;

		availableFundsAllocationValue.textContent = data ? formatCurrency(available) : '-';

		const labels = ['Stocks', 'Bonds', 'Mutual Funds'];
		const values = [stocks, bonds, mutualFunds];
		const colors = ['#2563eb', '#7c3aed', '#0d9488'];

		if (cash > 0) {
			labels.push('Cash');
			values.push(cash);
			colors.push('#0891b2');
		}

		labels.push('Available Funds');
		values.push(Math.max(available, 0));
		colors.push('#f59e0b');

		const hasData = values.some((value) => value > 0);

		if (allocationChartInstance) {
			allocationChartInstance.destroy();
			allocationChartInstance = null;
		}

		if (!hasData || typeof Chart === 'undefined') {
			allocationEmptyState.hidden = false;
			allocationChartCanvas.hidden = true;
			return;
		}

		allocationEmptyState.hidden = true;
		allocationChartCanvas.hidden = false;

		allocationChartInstance = new Chart(allocationChartCanvas, {
			type: 'doughnut',
			data: {
				labels,
				datasets: [{
					data: values,
					backgroundColor: colors,
					borderColor: '#ffffff',
					borderWidth: 2
				}]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				cutout: '58%',
				plugins: {
					legend: { display: false },
					tooltip: {
						callbacks: {
							label: (context) => `${context.label}: ${formatCurrency(context.raw)}`
						}
					}
				}
			}
		});
	}

	function renderPerformanceLoading() {
		performanceEmptyState.hidden = true;
		performanceTable.innerHTML = '';
	}

	function renderPerformanceError(message) {
		if (performanceChartInstance) {
			performanceChartInstance.destroy();
			performanceChartInstance = null;
		}
		performanceChartCanvas.hidden = true;
		performanceEmptyState.hidden = false;
		performanceEmptyState.textContent = message;
		performanceTable.innerHTML = '';
	}

	const PERIOD_META = {
		monthly: { subLabel: 'Last 6 months \u00b7 invested vs. current value' },
		quarterly: { subLabel: 'Last 4 quarters \u00b7 invested vs. current value' },
		yearly: { subLabel: 'Last 5 years \u00b7 invested vs. current value' }
	};

	function renderPerformance() {
		if (!performanceData) {
			renderPerformanceError('No performance data yet.');
			return;
		}

		const points = performanceData[activePerformancePeriod] || [];
		performanceSubLabel.textContent = PERIOD_META[activePerformancePeriod]?.subLabel || '';

		if (points.length === 0 || typeof Chart === 'undefined') {
			renderPerformanceError('No performance data yet.');
			return;
		}

		performanceEmptyState.hidden = true;
		performanceChartCanvas.hidden = false;

		const labels = points.map((point) => point.label);
		const investedSeries = points.map((point) => Number(point.invested ?? 0));
		const currentValueSeries = points.map((point) => Number(point.currentValue ?? 0));

		if (performanceChartInstance) {
			performanceChartInstance.destroy();
			performanceChartInstance = null;
		}

		performanceChartInstance = new Chart(performanceChartCanvas, {
			type: 'bar',
			data: {
				labels,
				datasets: [
					{
						label: 'Invested',
						data: investedSeries,
						backgroundColor: '#94a3b8',
						borderRadius: 4,
						maxBarThickness: 36
					},
					{
						label: 'Current Value',
						data: currentValueSeries,
						backgroundColor: '#2563eb',
						borderRadius: 4,
						maxBarThickness: 36
					}
				]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				interaction: { mode: 'index', intersect: false },
				plugins: {
					legend: { position: 'top', labels: { boxWidth: 12, font: { size: 11 } } },
					tooltip: {
						callbacks: {
							label: (context) => `${context.dataset.label}: ${formatCurrency(context.raw)}`
						}
					}
				},
				scales: {
					y: {
						beginAtZero: true,
						ticks: {
							callback: (value) => formatCurrency(value)
						}
					}
				}
			}
		});

		renderPerformanceTable(points);
	}

	function renderPerformanceTable(points) {
		performanceTable.innerHTML = points
			.map((point) => {
				const gainLoss = Number(point.gainLoss ?? 0);
				const label = gainLoss > 0 ? 'Gain' : (gainLoss < 0 ? 'Loss' : 'Neutral');
				const cssClass = gainLossClassFor(label);
				const sign = gainLoss > 0 ? '+' : '';

				return `
					<div class="performance-table-item">
						<div class="period-label">${escapeHtml(point.label)}</div>
						<div class="period-value ${cssClass}">${sign}${formatCurrency(gainLoss)}</div>
						<div class="period-value ${cssClass}" style="font-size:0.72rem;">${sign}${formatPercent(Number(point.returnPercent ?? 0))}%</div>
					</div>
				`;
			})
			.join('');
	}

	function setGainLossClass(label) {
		gainLossValue.classList.remove('gain-positive', 'gain-negative', 'gain-neutral');
		gainLossLabel.classList.remove('gain-positive', 'gain-negative', 'gain-neutral');

		if (label === 'Gain') {
			gainLossValue.classList.add('gain-positive');
			gainLossLabel.classList.add('gain-positive');
			return;
		}

		if (label === 'Loss') {
			gainLossValue.classList.add('gain-negative');
			gainLossLabel.classList.add('gain-negative');
			return;
		}

		gainLossValue.classList.add('gain-neutral');
		gainLossLabel.classList.add('gain-neutral');
	}

	function buildSingleMessageCard(label, message) {
		return `
			<div class="investor-item">
				<div class="investor-item-label">${escapeHtml(label)}</div>
				<div class="investor-item-value">${escapeHtml(message)}</div>
			</div>
		`;
	}

	async function handleViewInvestment(investmentId) {
		try {
			const investment = await fetchJson(
				`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/${encodeURIComponent(investmentId)}`
			);
			const normalized = normalizeInvestment(investment);
			const cached = investments.find((item) => item.investmentId === investmentId);
			if (cached) {
				normalized.tickerSymbol = normalized.tickerSymbol || cached.tickerSymbol;
				normalized.assetType = normalized.assetType || cached.assetType;
			}
			openViewModal(normalized);
		} catch (error) {
			openViewModal({
				investmentId,
				tickerSymbol: '-',
				assetType: '-',
				assetId: '-',
				quantity: 0,
				amountInvested: 0,
				currentValue: 0,
				purchaseDate: '-',
				error: error instanceof Error ? error.message : 'Failed to load investment details.'
			});
		}
	}

	function openViewModal(investment) {
		if (investment.error) {
			viewModalBody.innerHTML = `<p>${escapeHtml(investment.error)}</p>`;
		} else {
			const avgCost = getAverageCostFromInvestment(investment);
			const livePrice = getLivePriceFromInvestment(investment);
			const gainLoss = getGainLossInfo(investment);
			const gainLossClass = gainLossClassFor(gainLoss.label);

			viewModalBody.innerHTML = `
				<p><strong>Investment ID:</strong> ${escapeHtml(String(investment.investmentId))}</p>
				<p><strong>Portfolio ID:</strong> ${escapeHtml(String(portfolioId))}</p>
				<p><strong>Asset ID:</strong> ${escapeHtml(String(investment.assetId))}</p>
				<p><strong>Quantity Held:</strong> ${formatNumber(investment.quantity)}</p>
				<p><strong>Avg. Cost / Unit:</strong> ${formatCurrency(avgCost)}</p>
				<p><strong>Current Price / Unit:</strong> ${formatCurrency(livePrice)}</p>
				<p><strong>Total Invested:</strong> ${formatCurrency(investment.amountInvested)}</p>
				<p><strong>Current Value:</strong> ${formatCurrency(investment.currentValue)}</p>
				<p class="${gainLossClass}"><strong>Gain/Loss:</strong> ${formatCurrency(gainLoss.amount)} (${formatPercent(gainLoss.percent)}%)</p>
				<p><strong>Held Since:</strong> ${formatDate(investment.purchaseDate)}</p>
			`;
		}

		viewModal.hidden = false;
	}

	function closeViewModal() {
		viewModal.hidden = true;
	}

	function openFormModal(investment) {
		formMessage.textContent = '';
		formMessage.style.color = '#0f172a';
		formPurchaseDateInput.max = getTodayIsoDate();

		formModalTitle.textContent = 'Update Investment';
		submitFormBtn.textContent = 'Update';
		formInvestmentIdInput.value = String(investment.investmentId);
		formAssetIdInput.value = String(investment.assetId);
		formPurchasePriceInput.value = String(investment.purchasePrice);
		formQuantityInput.value = String(investment.quantity);
		formPurchaseDateInput.value = normalizeDateForInput(investment.purchaseDate);
		setMarketPriceMessage(`Current Market Price: ${formatCurrency(getLivePriceFromInvestment(investment))}`, false);

		formModal.hidden = false;
	}

	function closeFormModal() {
		formModal.hidden = true;
		investmentForm.reset();
		formAssetIdInput.value = '';
		setMarketPriceMessage('Current Market Price: -', false);
		clearFormMessage();
	}

	async function loadCurrentMarketPriceForSelectedAsset() {
		const assetId = Number(formAssetIdInput.value);
		if (!Number.isFinite(assetId) || assetId <= 0) {
			setMarketPriceMessage('Current Market Price: -', false);
			return;
		}

		const cached = livePriceCache.get(assetId);
		if (cached) {
			setMarketPriceMessage(`Current Market Price: ${formatCurrency(cached)}`, false);
			return;
		}

		setMarketPriceMessage('Current Market Price: Loading...', false);

		try {
			const quote = await fetchJson(
				`${API_BASE_URL}/assets/${encodeURIComponent(assetId)}/live-price`
			);
			const price = Number(quote.price ?? 0);
			if (!Number.isFinite(price) || price <= 0) {
				throw new Error('Current market price is unavailable for this asset.');
			}
			livePriceCache.set(assetId, price);
			setMarketPriceMessage(`Current Market Price: ${formatCurrency(price)}`, false);
		} catch (error) {
			setMarketPriceMessage(
				error instanceof Error ? error.message : 'Unable to load current market price.',
				true
			);
		}
	}


	function openTradeModal(mode, investment) {
		tradeModeInput.value = mode;
		tradeMessage.textContent = '';
		tradeMessage.style.color = '#0f172a';
		tradeForm.reset();
		tradeAssetIdInput.value = '';
		tradeEstimate.textContent = 'Estimated Total: -';
		setTradeLivePriceMessage('Current Market Price: -', false);

		if (mode === 'sell' && investment) {
			tradeModalTitle.textContent = 'Sell';
			submitTradeBtn.textContent = 'Sell';
			tradeInvestmentIdInput.value = String(investment.investmentId);
			tradeAssetIdInput.value = String(investment.assetId);
			tradeAssetIdInput.disabled = true;
			tradeQuantityInput.max = String(investment.quantity);
			tradeHoldingInfo.hidden = false;
			tradeHoldingInfo.textContent = `You currently hold ${formatNumber(investment.quantity)} units of ${investment.tickerSymbol || 'this asset'}.`;
			loadCurrentMarketPriceForTrade();
		} else {
			tradeModalTitle.textContent = 'Buy';
			submitTradeBtn.textContent = 'Buy';
			tradeInvestmentIdInput.value = '';
			tradeAssetIdInput.disabled = false;
			tradeQuantityInput.removeAttribute('max');
			tradeHoldingInfo.hidden = true;
			tradeHoldingInfo.textContent = '';
		}

		tradeModal.hidden = false;
	}

	function closeTradeModal() {
		tradeModal.hidden = true;
		tradeForm.reset();
		tradeAssetIdInput.disabled = false;
		tradeQuantityInput.removeAttribute('max');
		tradeHoldingInfo.hidden = true;
		setTradeLivePriceMessage('Current Market Price: -', false);
		tradeEstimate.textContent = 'Estimated Total: -';
		clearTradeMessage();
	}

	async function loadCurrentMarketPriceForTrade() {
		const assetId = Number(tradeAssetIdInput.value);
		if (!Number.isFinite(assetId) || assetId <= 0) {
			setTradeLivePriceMessage('Current Market Price: -', false);
			return;
		}

		const cached = livePriceCache.get(assetId);
		if (cached) {
			setTradeLivePriceMessage(`Current Market Price: ${formatCurrency(cached)}`, false);
			updateTradeEstimate();
			return;
		}

		setTradeLivePriceMessage('Current Market Price: Loading...', false);

		try {
			const quote = await fetchJson(
				`${API_BASE_URL}/assets/${encodeURIComponent(assetId)}/live-price`
			);
			const price = Number(quote.price ?? 0);
			if (!Number.isFinite(price) || price <= 0) {
				throw new Error('Current market price is unavailable for this asset.');
			}
			livePriceCache.set(assetId, price);
			setTradeLivePriceMessage(`Current Market Price: ${formatCurrency(price)}`, false);
			updateTradeEstimate();
		} catch (error) {
			setTradeLivePriceMessage(
				error instanceof Error ? error.message : 'Unable to load current market price.',
				true
			);
		}
	}

	function updateTradeEstimate() {
		const assetId = Number(tradeAssetIdInput.value);
		const quantity = Number(tradeQuantityInput.value);
		const price = livePriceCache.get(assetId);

		if (!Number.isFinite(price) || !Number.isFinite(quantity) || quantity <= 0) {
			tradeEstimate.textContent = 'Estimated Total: -';
			return;
		}

		tradeEstimate.textContent = `Estimated Total: ${formatCurrency(price * quantity)}`;
	}

	function validateTradePayload(payload, mode) {
		if (!Number.isFinite(payload.assetId) || payload.assetId <= 0) {
			return 'Please select an asset.';
		}

		if (!Number.isFinite(payload.quantity) || payload.quantity <= 0) {
			return 'Please enter a valid quantity.';
		}

		if (mode === 'sell') {
			const max = Number(tradeQuantityInput.max);
			if (Number.isFinite(max) && payload.quantity > max) {
				return `You only hold ${formatNumber(max)} units.`;
			}
		}

		return null;
	}

	async function buyInvestment(payload) {
		await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/buy`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(payload)
		});
	}

	async function sellInvestment(payload) {
		await fetchJson(`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/sell`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(payload)
		});
	}

	async function handleShowTransactions(investmentId) {
		transactionsModalBody.innerHTML = '<p>Loading...</p>';
		transactionsModal.hidden = false;

		try {
			const data = await fetchJson(
				`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/${encodeURIComponent(investmentId)}/transactions`
			);
			const transactions = Array.isArray(data) ? data : [];
			renderTransactions(transactions);
		} catch (error) {
			transactionsModalBody.innerHTML = `<p>${escapeHtml(error instanceof Error ? error.message : 'Unable to load transaction history.')}</p>`;
		}
	}

	function renderTransactions(transactions) {
		if (transactions.length === 0) {
			transactionsModalBody.innerHTML = '<p>No transactions recorded for this investment.</p>';
			return;
		}

		transactionsModalBody.innerHTML = transactions
			.map((transaction) => {
				const type = firstNonEmpty(transaction.transactionType, transaction.transaction_type);
				return `
					<p>
						<strong>${escapeHtml(type)}</strong>
						&mdash; ${formatNumber(Number(transaction.quantity ?? 0))} units
						@ ${formatCurrency(Number(transaction.pricePerUnit ?? transaction.price_per_unit ?? 0))}
						= ${formatCurrency(Number(transaction.transactionAmount ?? transaction.amount ?? 0))}
						on ${formatDate(transaction.transactionDate ?? transaction.transaction_date)}
					</p>
				`;
			})
			.join('');
	}

	function closeTransactionsModal() {
		transactionsModal.hidden = true;
		transactionsModalBody.innerHTML = '';
	}

	function setTradeMessage(message, isError) {
		tradeMessage.textContent = message;
		tradeMessage.style.color = isError ? '#b91c1c' : '#166534';
	}

	function clearTradeMessage() {
		tradeMessage.textContent = '';
		tradeMessage.style.color = '#0f172a';
	}

	function setTradeLivePriceMessage(message, isError) {
		tradeLivePrice.textContent = message;
		tradeLivePrice.style.color = isError ? '#b91c1c' : '#334155';
	}

	function validateFormPayload(payload, investmentId) {
		if (!Number.isFinite(payload.assetId) || payload.assetId <= 0) {
			return 'Please select an asset.';
		}

		if (!Number.isFinite(payload.purchasePrice) || payload.purchasePrice < 0) {
			return 'Please enter a valid purchase price.';
		}

		if (!Number.isFinite(payload.quantity) || payload.quantity <= 0) {
			return 'Please enter a valid quantity.';
		}

		if (!payload.purchaseDate) {
			return 'Please choose a purchase date.';
		}

		if (payload.purchaseDate > getTodayIsoDate()) {
			return 'Purchase date cannot be later than today.';
		}

		if (!summary) {
			return null;
		}

		const totalFunds = Number(summary.totalFunds ?? 0);
		const currentUsed = Number(summary.usedFunds ?? 0);
		const nextAmount = payload.purchasePrice * payload.quantity;
		const oldInvestment = investments.find((item) => item.investmentId === investmentId);

		const nextUsed = currentUsed + nextAmount - (oldInvestment ? oldInvestment.amountInvested : 0);
		if (nextUsed > totalFunds) {
			return 'Investment exceeds available portfolio funds.';
		}

		return null;
	}

	async function updateInvestment(investmentId, payload) {
		if (!Number.isFinite(investmentId)) {
			throw new Error('Invalid investment ID for update.');
		}

		await fetchJson(
			`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/${encodeURIComponent(investmentId)}`,
			{
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(payload)
			}
		);
	}

	async function handleDeleteInvestment(investmentId) {
		if (!window.confirm('Delete this investment?')) {
			return;
		}

		try {
			await fetchJson(
				`${API_BASE_URL}/portfolios/${encodeURIComponent(portfolioId)}/investments/${encodeURIComponent(investmentId)}`,
				{ method: 'DELETE' }
			);

			await Promise.all([loadInvestments(), loadPortfolioSummary()]);
		} catch (error) {
			window.alert(error instanceof Error ? error.message : 'Failed to delete investment.');
		}
	}

	function normalizeInvestment(raw) {
		return {
			investmentId: Number(raw.investmentId ?? raw.investment_id ?? 0),
			portfolioId: Number(raw.portfolioId ?? raw.portfolio_id ?? portfolioId),
			assetId: Number(raw.assetId ?? raw.asset_id ?? 0),
			purchasePrice: Number(raw.purchasePrice ?? raw.purchase_price ?? 0),
			quantity: Number(raw.quantity ?? 0),
			amountInvested: Number(raw.amountInvested ?? raw.amount_invested ?? 0),
			currentValue: Number(raw.currentValue ?? raw.current_value ?? 0),
			purchaseDate: raw.purchaseDate ?? raw.purchase_date ?? '',
			tickerSymbol: firstNonEmpty(raw.tickerSymbol, raw.assetTickerSymbol, raw.ticker),
			assetType: firstNonEmpty(raw.assetType, raw.asset_type)
		};
	}

	function normalizeAssetType(value) {
		if (typeof value !== 'string') {
			return null;
		}

		const normalized = value.trim().toLowerCase();
		if (normalized === 'stocks' || normalized === 'stock') {
			return 'stocks';
		}
		if (normalized === 'bonds' || normalized === 'bond') {
			return 'bonds';
		}
		if (normalized === 'mutual funds' || normalized === 'mutual fund') {
			return 'mutual funds';
		}
		if (normalized === 'cash') {
			return 'cash';
		}

		return null;
	}

	function formatAssetTypeLabel(type) {
		const normalized = normalizeAssetType(type);
		return normalized ? ASSET_TYPE_LABELS[normalized] : firstNonEmpty(type);
	}


	function setFormMessage(message, isError) {
		formMessage.textContent = message;
		formMessage.style.color = isError ? '#b91c1c' : '#166534';
	}

	function clearFormMessage() {
		formMessage.textContent = '';
		formMessage.style.color = '#0f172a';
	}

	function setMarketPriceMessage(message, isError) {
		formLiveMarketPrice.textContent = message;
		formLiveMarketPrice.style.color = isError ? '#b91c1c' : '#334155';
	}

	function openAddFundsModal() {
		addFundsForm.reset();
		fundDateInput.max = getTodayIsoDate();
		fundStatusInput.value = 'Completed';
		clearAddFundsMessage();
		addFundsModal.hidden = false;
	}

	function closeAddFundsModal() {
		addFundsModal.hidden = true;
		addFundsForm.reset();
		clearAddFundsMessage();
	}

	function setAddFundsMessage(message, isError) {
		addFundsMessage.textContent = message;
		addFundsMessage.style.color = isError ? '#b91c1c' : '#166534';
	}

	function clearAddFundsMessage() {
		addFundsMessage.textContent = '';
		addFundsMessage.style.color = '#0f172a';
	}

});

function getPortfolioId() {
	const params = new URLSearchParams(window.location.search);
	const fromUrl = params.get('portfolioId');
	return fromUrl && fromUrl.trim() ? fromUrl.trim() : '1';
}

function getTodayIsoDate() {
	const now = new Date();
	const month = String(now.getMonth() + 1).padStart(2, '0');
	const day = String(now.getDate()).padStart(2, '0');
	return `${now.getFullYear()}-${month}-${day}`;
}

async function fetchJson(url, options) {
	const response = await fetch(url, options);
	const text = await response.text();
	const payload = text ? safeJsonParse(text) : null;

	if (!response.ok) {
		if (payload && typeof payload === 'object') {
			const detailMessage = firstNonEmpty(payload.detail, payload.message, payload.error);
			if (detailMessage !== '-') {
				throw new Error(detailMessage);
			}
		}

		if (typeof payload === 'string' && payload.trim()) {
			throw new Error(payload);
		}
		throw new Error(`Request failed with status ${response.status}.`);
	}

	if (payload === null) {
		return {};
	}

	return payload;
}

function safeJsonParse(text) {
	try {
		return JSON.parse(text);
	} catch (error) {
		return text;
	}
}

function firstNonEmpty(...values) {
	for (const value of values) {
		if (typeof value === 'string' && value.trim()) {
			return value.trim();
		}
	}
	return '-';
}

function formatCurrency(value) {
	return new Intl.NumberFormat('en-IN', {
		style: 'currency',
		currency: 'INR',
		maximumFractionDigits: 2
	}).format(Number.isFinite(value) ? value : 0);
}

function formatPercent(value) {
	if (!Number.isFinite(value)) {
		return '0.00';
	}
	return value.toFixed(2);
}

function formatNumber(value) {
	if (!Number.isFinite(value)) {
		return '0';
	}
	return new Intl.NumberFormat('en-IN', {
		maximumFractionDigits: 4
	}).format(value);
}

function getLivePriceFromInvestment(investment) {
	if (!Number.isFinite(investment.quantity) || investment.quantity <= 0) {
		return 0;
	}
	return investment.currentValue / investment.quantity;
}

function getAverageCostFromInvestment(investment) {
	if (!Number.isFinite(investment.quantity) || investment.quantity <= 0) {
		return 0;
	}
	return investment.amountInvested / investment.quantity;
}

function getGainLossInfo(investment) {
	const invested = Number.isFinite(investment.amountInvested) ? investment.amountInvested : 0;
	const current = Number.isFinite(investment.currentValue) ? investment.currentValue : 0;
	const amount = current - invested;
	const percent = invested > 0 ? (amount / invested) * 100 : 0;

	let label = 'Neutral';
	if (amount > 0) {
		label = 'Gain';
	} else if (amount < 0) {
		label = 'Loss';
	}

	return { amount, percent, label };
}

function gainLossClassFor(label) {
	if (label === 'Gain') {
		return 'gain-positive';
	}
	if (label === 'Loss') {
		return 'gain-negative';
	}
	return 'gain-neutral';
}

function formatDate(value) {
	if (!value) {
		return '-';
	}

	const parsed = new Date(value);
	if (Number.isNaN(parsed.getTime())) {
		return value;
	}

	return parsed.toLocaleDateString('en-GB');
}

function normalizeDateForInput(value) {
	if (!value) {
		return '';
	}
	const parsed = new Date(value);
	if (Number.isNaN(parsed.getTime())) {
		return '';
	}
	return parsed.toISOString().slice(0, 10);
}

function escapeHtml(value) {
	return String(value)
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/"/g, '&quot;')
		.replace(/'/g, '&#39;');
}
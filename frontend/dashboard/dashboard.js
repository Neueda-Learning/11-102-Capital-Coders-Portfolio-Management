const params = new URLSearchParams(window.location.search);
//const empId = params.get("empid") || localStorage.getItem("empId") || "1";
const empId = params.get("employeeId") || localStorage.getItem("empId");
const portfolioList = document.getElementById("portfolioList");
const portfolioCount = document.getElementById("portfolioCount");
const employeeIdDisplay = document.getElementById("employeeIdDisplay");
const empIdSpan = document.getElementById("empIdSpan");
const employeeName = document.getElementById("employeeName");
const employeeEmail = document.getElementById("employeeEmail");
const modal = document.getElementById("modal");
const modalTitle = document.getElementById("modalTitle");
const portfolioForm = document.getElementById("portfolioForm");
const closeModalBtn = document.getElementById("closeModalBtn");
const cancelBtn = document.getElementById("cancelBtn");

const addBtn = document.getElementById("addBtn");

const portfolioNameInput = document.getElementById("portfolioName");
const portfolioDescriptionInput = document.getElementById("portfolioDescription");
const riskLevelInput = document.getElementById("riskLevel");
const allocatedAmountInput = document.getElementById("allocatedAmount");
const investorIdInput = document.getElementById("investorId");
const createdDateInput = document.getElementById("createdDate");


let portfolios = [];
let selectedPortfolio = null;
let formMode = "add";


employeeIdDisplay.textContent = empId;
empIdSpan.textContent = empId;



function getApiBase() {
    return "http://localhost:8080";
}

function showToast(message, type) {

    const toast = document.getElementById("toast");

    toast.textContent = message;

    toast.className = "toast " + type + " show";


    setTimeout(() => {

        toast.className = "toast";

    }, 3000);

}


function openModal(mode, portfolio = null) {

    formMode = mode;

    modalTitle.textContent =
        mode === "add" ? "Add Portfolio" : "Update Portfolio";


    if (portfolio) {

        portfolioNameInput.value = portfolio.portfolioName || "";
        portfolioDescriptionInput.value = portfolio.portfolioDescription || "";
        riskLevelInput.value = portfolio.riskLevel || "";
        allocatedAmountInput.value = portfolio.allocatedAmount || "";
        investorIdInput.value = portfolio.investorId || "";
        createdDateInput.value = portfolio.createdDate || "";

    }
    else {

        portfolioForm.reset();
        createdDateInput.value = new Date().toISOString().split("T")[0];

    }


    modal.classList.remove("hidden");
}



function closeModal() {

    modal.classList.add("hidden");

}




// DO NOT CHANGE THIS LOAD FUNCTION

async function loadPortfolios() {

    try {

        const res = await fetch(
            `${getApiBase()}/portfolios/employees/${empId}`
        );


        if (res.status != 200)
            throw new Error("Failed to fetch portfolios");


        portfolios = await res.json();


        renderPortfolios();


    }
    catch (err) {

        portfolioList.innerHTML =
            `<div class="details-item">Unable to load portfolios.</div>`;

        portfolioCount.textContent = "0 portfolios";

    }

}




// DELETE FUNCTION

async function deletePortfolio(id) {

    try {

        const res = await fetch(
            `${getApiBase()}/portfolios/${id}`,
            {
                method: "DELETE"
            }
        );


        if (!res.ok)
            throw new Error("Delete failed");


        await loadPortfolios();

        showToast(
            "Portfolio deleted successfully",
            "success"
        );

    }
    catch(error){

        showToast(
            "Unable to delete portfolio",
            "error"
        );

    }

}




// ONLY ONE RENDER FUNCTION

function renderPortfolios() {


    portfolioCount.textContent =
        `${portfolios.length} portfolios`;


    if (!portfolios.length) {

        portfolioList.innerHTML =
            `<div class="details-item">No portfolios available.</div>`;

        return;

    }



    portfolioList.innerHTML = portfolios
        .map((p)=>`

        <div class="portfolio-card" data-id="${p.portfolioId}">


            <div class="card-actions">
            <button
                class="icon-btn view-btn"
                data-id="${p.portfolioId}"
                title="View Portfolio">
                <i class="fa-solid fa-eye"></i>
            </button>

            <button
                class="icon-btn edit-btn"
                data-id="${p.portfolioId}"
                title="Update">
                <i class="fa-solid fa-pen-to-square"></i>
            </button>


            <button
                class="icon-btn delete-btn"
                data-id="${p.portfolioId}"
                title="Delete">
                <i class="fa-solid fa-trash"></i>
            </button>


            </div>



            <div class="profile-row">

                <div class="profile-icon">
                    ${(p.portfolioName || "P")
                    .charAt(0)
                    .toUpperCase()}
                </div>


                <div>

                    <h4>${p.portfolioName}</h4>


                    <p>ID: ${p.portfolioId}</p>

                    <p>
                    Risk: ${p.riskLevel || "-"}
                    </p>

                </div>


            </div>

            <div class="portfolio-metrics" id="metrics-${p.portfolioId}">
                <span class="metric-loading">Loading gain/return…</span>
            </div>


        </div>

        `)
        .join("");


    // GAIN / RETURN METRICS (fetched per portfolio, doesn't block card render)

    portfolios.forEach((p) => {

        fetch(`${getApiBase()}/portfolios/${p.portfolioId}/summary`)
            .then((res) => {
                if (!res.ok) throw new Error("Summary fetch failed");
                return res.json();
            })
            .then((summary) => {

                const metricsEl =
                    document.getElementById(`metrics-${p.portfolioId}`);

                if (!metricsEl) return;

                const gainClass =
                    summary.gainLoss > 0 ? "positive"
                    : summary.gainLoss < 0 ? "negative"
                    : "neutral";

                metricsEl.innerHTML = `
                    <span class="metric ${gainClass}">
                        ${summary.gainLossLabel}: ${summary.gainLoss.toFixed(2)}
                    </span>
                    <span class="metric ${gainClass}">
                        Return: ${summary.returnPercent.toFixed(2)}%
                    </span>
                `;

            })
            .catch(() => {

                const metricsEl =
                    document.getElementById(`metrics-${p.portfolioId}`);

                if (metricsEl)
                    metricsEl.innerHTML =
                        `<span class="metric-loading">Metrics unavailable</span>`;

            });

    });




     // VIEW BUTTON

     document.querySelectorAll(".view-btn")
     .forEach(btn=>{

         btn.addEventListener("click",(e)=>{

             e.stopPropagation();

             const portfolioId = btn.dataset.id;


//             window.location.href =
//             `portfolio.html?portfolioId=${portfolioId}`;
              window.location.href =
              `../portfolio/portfolio.html?portfolioId=${portfolioId}`;

         });

     });
    // UPDATE BUTTON

    document.querySelectorAll(".edit-btn")
    .forEach(btn=>{


        btn.addEventListener("click",(e)=>{


            e.stopPropagation();


            const id = Number(btn.dataset.id);



            const portfolio =
                portfolios.find(
                    p=>p.portfolioId === id
                );



            selectedPortfolio = portfolio;


            openModal(
                "update",
                portfolio
            );


        });


    });






    // DELETE BUTTON

    document.querySelectorAll(".delete-btn")
    .forEach(btn=>{


        btn.addEventListener("click",async(e)=>{


            e.stopPropagation();



            const id =
                Number(btn.dataset.id);



            const confirmDelete =
                confirm(
                "Are you sure you want to delete this portfolio?"
                );



            if(confirmDelete){

                await deletePortfolio(id);

            }


        });


    });



}






// ADD BUTTON

addBtn.addEventListener(
"click",
()=>openModal("add")
);






// ADD + UPDATE SUBMIT

portfolioForm.addEventListener(
"submit",
async(e)=>{


    e.preventDefault();



    const payload = {

        empId:Number(empId),

        investorId:Number(
            investorIdInput.value
        ),

        portfolioName:
        portfolioNameInput.value.trim(),


        portfolioDescription:
        portfolioDescriptionInput.value.trim(),


        riskLevel:
        riskLevelInput.value.trim(),


        allocatedAmount:
        Number(allocatedAmountInput.value),


        createdDate:
        createdDateInput.value

    };



    const url =
    formMode === "add"

    ?
    `${getApiBase()}/portfolios`

    :
    `${getApiBase()}/portfolios/${selectedPortfolio.portfolioId}`;




    const method =
    formMode === "add"
    ?
    "POST"
    :
    "PUT";




    try {


        const res = await fetch(
            url,
            {

                method:method,

                headers:{
                    "Content-Type":
                    "application/json"
                },

                body:
                JSON.stringify(payload)

            }
        );



        if(!res.ok)
            throw new Error("Save failed");



        closeModal();

        selectedPortfolio=null;


        await loadPortfolios();
        if(formMode === "add") {

            showToast(
                "Portfolio added successfully",
                "success"
            );

        }
        else {

            showToast(
                "Portfolio updated successfully",
                "success"
            );

        }

        }
        catch(error){

            showToast(
                "Unable to save portfolio",
                "error"
            );

        }

        });






closeModalBtn.addEventListener(
"click",
closeModal
);



cancelBtn.addEventListener(
"click",
closeModal
);



modal.addEventListener(
"click",
(e)=>{

    if(e.target === modal)
        closeModal();

});





document.querySelectorAll(".nav-item")
.forEach((btn)=>{


    btn.addEventListener(
    "click",
    ()=>{


        document
        .querySelectorAll(".nav-item")
        .forEach((b)=>
            b.classList.remove("active")
        );


        btn.classList.add("active");


    });


});

async function loadEmployeeDetails(){

    try{

        const res = await fetch(
            `${getApiBase()}/employees/${empId}`
        );


        if(!res.ok)
            throw new Error("Employee fetch failed");


        const employee = await res.json();


        employeeName.textContent =
            employee.empName || "-";


        employeeEmail.textContent =
            employee.empEmail || "-";


    }
    catch(error){

        employeeName.textContent = "Not Available";
        employeeEmail.textContent = "Not Available";

    }

}

// NEWS: company filter + list

const newsList = document.getElementById("newsList");
const newsCompanyFilter = document.getElementById("newsCompanyFilter");

function timeAgo(publishedAt) {

    if (!publishedAt) return "";

    const published = new Date(publishedAt);
    if (isNaN(published)) return "";

    const diffMs = Date.now() - published.getTime();
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

    if (diffHours < 1) return "Just now";
    if (diffHours < 24) return `${diffHours}h ago`;

    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d ago`;
}

async function loadCompanyOptions() {

    try {

        const res = await fetch(`${getApiBase()}/assets`);

        if (!res.ok)
            throw new Error("Failed to fetch assets");

        const assets = await res.json();

        const seen = new Set();

        assets.forEach((asset) => {

            const name = asset.assetName;

            if (!name || seen.has(name))
                return;

            seen.add(name);

            const option = document.createElement("option");
            option.value = name;
            option.textContent = `${name} (${asset.tickerSymbol || "-"})`;
            newsCompanyFilter.appendChild(option);

        });

    }
    catch (err) {
        // company filter is optional - dashboard still works with "All markets"
    }

}

async function loadNews(company = "") {

    newsList.innerHTML = `<div class="details-item">Loading news…</div>`;

    try {

        const url = company
            ? `${getApiBase()}/news?company=${encodeURIComponent(company)}`
            : `${getApiBase()}/news`;

        const res = await fetch(url);

        if (!res.ok)
            throw new Error("Failed to fetch news");

        const articles = await res.json();

        if (!articles.length) {
            newsList.innerHTML = `<div class="details-item">No news found.</div>`;
            return;
        }

        newsList.innerHTML = articles
            .map((a) => `
            <a class="news-card" href="${a.url || "#"}" target="_blank" rel="noopener noreferrer">
                ${a.imageUrl
                    ? `<img class="news-thumb" src="${a.imageUrl}" alt="" onerror="this.style.display='none'" />`
                    : ""}
                <div class="news-body">
                    <h4>${a.title || "Untitled"}</h4>
                    <p>${a.description || ""}</p>
                    <span class="muted">${a.sourceName || "Unknown source"} · ${timeAgo(a.publishedAt)}</span>
                </div>
            </a>
            `)
            .join("");

    }
    catch (err) {

        newsList.innerHTML =
            `<div class="details-item">Unable to load news right now.</div>`;

    }

}

newsCompanyFilter.addEventListener("change", () => {
    loadNews(newsCompanyFilter.value);
});

loadPortfolios();
loadEmployeeDetails();
loadCompanyOptions();
loadNews();
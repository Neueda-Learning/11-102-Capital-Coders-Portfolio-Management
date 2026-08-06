document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const employeeIdInput = document.getElementById('employeeId');

    if (!form || !employeeIdInput) {
        return;
    }

    form.addEventListener('submit', (event) => {
        event.preventDefault();

        const employeeId = employeeIdInput.value.trim();

        if (!employeeId) {
            employeeIdInput.focus();
            return;
        }

//        const nextPage = `../dashboard/dashboard.html?employeeId=${encodeURIComponent(employeeId)}`;
//        window.location.href = nextPage;
     localStorage.setItem("empId", employeeId);

     const nextPage =
     `../dashboard/dashboard.html?employeeId=${encodeURIComponent(employeeId)}`;

     window.location.href = nextPage;
    });
});

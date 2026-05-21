document.addEventListener("DOMContentLoaded", function () {
    // Monthly Revenue Line Chart
    const revenueCtx = document.getElementById('revenueChart').getContext('2d');

    const revenueChart = new Chart(revenueCtx, {
        type: 'line',
        data: {
            labels: [
                'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
                'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
            ],
            datasets: [{
                label: 'Revenue ($)',
                data: [1200, 1500, 1800, 2000, 2500, 2700, 3200, 3000, 3400, 3800, 4000, 4200],
                fill: true,
                borderColor: '#4CAF50',
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
                tension: 0.4,
                pointRadius: 4,
                pointBackgroundColor: '#4CAF50'
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        color: '#333'
                    }
                },
                x: {
                    ticks: {
                        color: '#333'
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        color: '#333'
                    }
                }
            }
        }
    });

    // Booking Status Doughnut Chart
    const statusCtx = document.getElementById('statusChart').getContext('2d');

    const statusChart = new Chart(statusCtx, {
        type: 'doughnut',
        data: {
            labels: ['Completed', 'Pending', 'Cancelled'],
            datasets: [{
                label: 'Booking Status',
                data: [65, 20, 15],
                backgroundColor: ['#4CAF50', '#FFC107', '#F44336'],
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#333'
                    }
                }
            }
        }
    });
});

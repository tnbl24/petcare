const selectElement = document.getElementById('years');
const currentYear = new Date().getFullYear();
const startYear = 2022;
for (let year = currentYear; year >= startYear; year--) {
    const option = document.createElement('option');
    option.value = year;
    option.textContent = year;
    selectElement.appendChild(option);
}
async function fetchGroupServiceCounts() {
    try {
        const response = await fetch('/admin/getGroupServiceCounts');
        const data = await response.json();

        const totalCount = data.reduce((sum, item) => sum + item.bookedCount, 0);
        const tableBody = document.querySelector('.table-borderless'); // Giữ nguyên lớp bảng
        tableBody.innerHTML = ''; // Xóa nội dung cũ trước khi thêm mới

        data.forEach(item => {
            const row = document.createElement('tr');
            const nameCell = document.createElement('td');
            nameCell.className = 'custom-col-name'; // Đổi tên lớp cho ô tên
            nameCell.textContent = item.name;

            const progressCell = document.createElement('td');
            progressCell.className = 'custom-col-progress'; // Đổi tên lớp cho ô tiến độ

            // Tạo div cho thanh tiến độ
            const progressDiv = document.createElement('div');
            progressDiv.style.backgroundColor = '#e0e0e0'; // Màu nền xám để thể hiện 100%
            progressDiv.style.borderRadius = '0.5rem'; // Bo góc cho div tiến độ
            progressDiv.style.height = '0.6rem'; // Đặt chiều cao cho div tiến độ
            progressDiv.style.width = '150px'; // Đặt chiều dài cho div tiến độ
            progressDiv.style.position = 'relative'; // Cần thiết để định vị thanh xanh
            progressDiv.style.padding = '0'; // Đặt padding là 0

            // Tạo thanh tiến độ
            const progressBar = document.createElement('div');
            progressBar.className = 'progress-bar progress-label'; // Giữ lớp để hiển thị phần trăm
            progressBar.style.height = '100%'; // Chiều cao 100% của thanh tiến độ
            progressBar.style.borderRadius = '0.5rem'; // Bo góc cho thanh tiến độ
            progressBar.style.backgroundColor = '#00CFDD'; // Màu sắc thanh tiến độ
            progressBar.style.boxShadow = '0px 2px 3px rgba(0, 207, 221, 0.8)'; // Đổ bóng
            progressBar.style.position = 'absolute'; // Định vị tuyệt đối để nằm bên trong progressDiv
            progressBar.style.top = '0'; // Đặt thanh xanh ở phía trên cùng của div xám
            progressBar.style.left = '0'; // Đặt thanh xanh ở phía bên trái của div xám
            progressBar.style.padding = '0'; // Đặt padding là 0

            const bookedCount = item.bookedCount;
            const progressPercentage = totalCount > 0 ? (bookedCount / totalCount) * 100 : 0;

            progressBar.style.width = `${progressPercentage}%`; // Đặt chiều rộng thanh theo phần trăm
            progressBar.setAttribute('role', 'progressbar');
            progressBar.setAttribute('aria-valuenow', bookedCount);
            progressBar.setAttribute('aria-valuemin', '0');
            progressBar.setAttribute('aria-valuemax', totalCount);

            // Gán phần trăm vào ô phần trăm
            const percentCell = document.createElement('td');
            percentCell.className = 'custom-col-percent text-center'; // Đổi tên lớp cho ô phần trăm
            percentCell.textContent = `${progressPercentage.toFixed(0)}%`;

            // Thêm thanh tiến độ vào div và div vào ô tiến độ
            progressDiv.appendChild(progressBar);
            progressCell.appendChild(progressDiv);

            // Thêm các ô vào hàng
            row.appendChild(nameCell);
            row.appendChild(progressCell);
            row.appendChild(percentCell);
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

const cancelledOp = {
    chart: {
        height: "50%",
        maxWidth: "100%",
        type: "area",
        fontFamily: "Inter, sans-serif",
        dropShadow: {
            enabled: true,
            blur: 3,
            opacity: 0.2,
            color: '#dc3545',
        },
        toolbar: {
            show: false,
        },
    },
    tooltip: {
        enabled: true,
        x: {
            show: false,
        },
        y: {
            formatter: function (val) {
                return Math.floor(val); // Chuyển đổi số thực thành số nguyên
            },
        },
    },
    fill: {
        type: "gradient",
        gradient: {
            opacityFrom: 0,
            opacityTo: 0,
            shade: "#dc3545",
            gradientToColors: ["#dc3545"],
        },
    },
    dataLabels: {
        enabled: false,
    },
    stroke: {
        width: 4,
        colors: ["#dc3545"],
    },
    grid: {
        show: false,
        strokeDashArray: 4,
        padding: {
            left: 2,
            right: 2,
            top: 0
        },
    },
    series: [
        {
            name: "Đã hủy",
            data: [], // Dữ liệu sẽ được cập nhật từ API
        },
    ],
    xaxis: {
        categories: ['01 February', '02 February', '03 February', '04 February', '05 February', '06 February', '07 February'],
        labels: {
            show: false,
        },
        axisBorder: {
            show: false,
        },
        axisTicks: {
            show: false,
        },
    },
    yaxis: {
        show: false,
    },
};
const completedOp = {
    chart: {
        height: "50%",
        maxWidth: "100%",
        type: "area",
        fontFamily: "Inter, sans-serif",
        dropShadow: {
            enabled: true,
            blur: 3,
            opacity: 0.2,
            color: '#28A745',
        },
        toolbar: {
            show: false,
        },
    },
    tooltip: {
        enabled: true,
        x: {
            show: false,
        },
        y: {
            formatter: function (val) {
                return Math.floor(val); // Chuyển đổi số thực thành số nguyên
            },
        },
    },
    fill: {
        type: "gradient",
        gradient: {
            opacityFrom: 0,
            opacityTo: 0,
            shade: "#28A745",
            gradientToColors: ["#28A745"],
        },
    },
    dataLabels: {
        enabled: false,
    },
    stroke: {
        width: 4,
        colors: ["#28A745"],
    },
    grid: {
        show: false,
        strokeDashArray: 4,
        padding: {
            left: 2,
            right: 2,
            top: 0
        },
    },
    series: [
        {
            name: "Hoàn thành",
            data: [],
        },
    ],
    xaxis: {
        categories: ['01 February', '02 February', '03 February', '04 February', '05 February', '06 February', '07 February'],
        labels: {
            show: false,
        },
        axisBorder: {
            show: false,
        },
        axisTicks: {
            show: false,
        },
    },
    yaxis: {
        show: false,
    },
};
let completedChart, cancelledChart;

if (document.getElementById("completed-chart") && typeof ApexCharts !== 'undefined') {
    completedChart = new ApexCharts(document.getElementById("completed-chart"), completedOp);
    completedChart.render();
}
if (document.getElementById("cancelled-chart") && typeof ApexCharts !== 'undefined') {
    cancelledChart = new ApexCharts(document.getElementById("cancelled-chart"), cancelledOp);
    cancelledChart.render();
}

document.addEventListener('DOMContentLoaded', () => {
    fetchBookedServiceCounts();
    fetchGroupServiceCounts();
    updateMonthlyCounts();
});

async function fetchBookedServiceCounts() {
    try {
        const response = await fetch('/admin/getBookedServiceCountByStatus');
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();

        console.log(data);

        document.querySelector('.pending-count').textContent = data.pending || 0;
        document.querySelector('.confirmed-count').textContent = data.confirmed || 0;

        const completedData = Object.values(data.completed).map(value => parseInt(value, 10)); // Chuyển đổi thành số nguyên
        const cancelledData = Object.values(data.cancelled).map(value => parseInt(value, 10)); // Chuyển đổi thành số nguyên

        completedChart.updateSeries([{ name: "Hoàn thành", data: completedData }]);
        cancelledChart.updateSeries([{ name: "Đã hủy", data: cancelledData }]);

        const months = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'];

        completedChart.updateOptions({
            xaxis: {
                categories: months,
            },
        });

        cancelledChart.updateOptions({
            xaxis: {
                categories: months,
            },
        });

    } catch (error) {
        console.error('Error fetching booked service counts:', error);
    }
}

async function updateMonthlyCounts() {
    try {
        const response = await fetch('/admin/getBookedServiceCountByStatus');
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();

        const currentMonth = new Date().getMonth() + 1;
        const lastMonth = currentMonth === 1 ? 12 : currentMonth - 1;

        const completedCount = data.completed[currentMonth] || 0;
        const lastMonthCompleted = data.completed[lastMonth] || 0;
        const completedPercent = ((completedCount - lastMonthCompleted) / (lastMonthCompleted || 1) * 100).toFixed(0);

        const completedCountElem = document.querySelector('.completed-count');
        const completedPercentElem = document.querySelector('.completed-percent');

        if (completedCountElem) {
            completedCountElem.textContent = completedCount;
        }
        if (completedPercentElem) {
            completedPercentElem.textContent = `${completedPercent}%`;
        }

        const cancelledCount = data.cancelled[currentMonth] || 0;
        const lastMonthCancelled = data.cancelled[lastMonth] || 0;
        const cancelledPercent = ((cancelledCount - lastMonthCancelled) / (lastMonthCancelled || 1) * 100).toFixed(0);

        const cancelledCountElem = document.querySelector('.cancelled-count');
        const cancelledPercentElem = document.querySelector('.cancelled-percent');

        if (cancelledCountElem) {
            cancelledCountElem.textContent = cancelledCount;
        }
        if (cancelledPercentElem) {
            cancelledPercentElem.textContent = `${cancelledPercent}%`;
        }

    } catch (error) {
        console.error('Error updating monthly counts:', error);
    }
}

let area;

// Hàm để lấy tổng doanh thu theo năm đã chọn và cập nhật biểu đồ
async function fetchTotalPriceByYear(year) {
    try {
        const response = await fetch(`/admin/getTotalPriceByYearAndMonth/${year}`);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const data = await response.json();

        console.log(data);

        document.querySelector('.card-header h4').textContent = `Tổng doanh thu: ${data.totalPrice.toLocaleString()} VND`;

        const onlinePayments = Array.from({ length: 12 }, (_, i) =>
            data.priceByMonth[i + 1] || 0
        );

        if (area) {
            area.updateOptions({
                series: [
                    {
                        name: "Doanh thu",
                        data: onlinePayments,
                    },
                ],
            });
        }
    } catch (error) {
        console.error('Error fetching total price by year:', error);
    }
}

const areaOptions = {
    series: [
        {
            name: "Doanh thu",
            data: [],
        },
    ],
    chart: {
        height: 350,
        type: "area",
    },
    dataLabels: {
        enabled: false,
    },
    stroke: {
        curve: "smooth",
    },
    xaxis: {
        categories: [
            "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
            "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
            "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        ],

    },
    tooltip: {
        x: {
            format: "dd/MM/yy",
        },
        y: {
            formatter: function (val) {
                return val.toLocaleString('vi-VN');
            },
        },
    },
};

area = new ApexCharts(document.querySelector("#area"), areaOptions);
area.render();

selectElement.addEventListener('change', (event) => {
    const selectedYear = event.target.value;
    fetchTotalPriceByYear(selectedYear);
});

fetchTotalPriceByYear(currentYear);
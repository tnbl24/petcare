function cal() {
    fetchBookingStats();
}

function fetchBookingStats() {
    const url = '/admin/booked-service-ctrl/all';

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data && data.length > 0) {
                createModal(data);
            } else {
                console.error('No booking data found');
                alert('Không có dữ liệu đặt chỗ.');
            }
        })
        .catch(error => console.error('Error fetching bookings:', error));
}

function createModal(bookings) {
    const modalHtml = `
        <div class="modal fade" id="bookingModal" tabindex="-1" role="dialog" aria-labelledby="modalTitle" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="modalTitle">Thống kê lịch</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <div class="form-inline mb-3">
                            <label for="monthSelect" class="mr-2">Chọn tháng:</label>
                            <select id="monthSelect" class="form-control mr-2">
                                ${generateMonthOptions()}
                            </select>
                            <label for="yearSelect" class="mr-2">Chọn năm:</label>
                            <select id="yearSelect" class="form-control">
                                ${generateYearOptions()}
                            </select>
                        </div>
                        <div class="status-legend mb-3">
                            <div>
                                <span style="color: orange;">● Chờ xác nhận</span>
                                <span style="color: blue;">● Đã xác nhận</span>
                                <span style="color: green;">● Đã hoàn thành</span>
                                <span style="color: red;">● Đã hủy</span>
                            </div>
                        </div>
                        <div id="dailyStats">
                            <!-- Dữ liệu sẽ được chèn vào đây -->
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
    $('#bookingModal').modal('show');

    $('#bookingModal').on('hidden.bs.modal', function () {
        $(this).remove();
    });

    const currentDate = new Date();
    document.getElementById('monthSelect').value = currentDate.getMonth() + 1;
    document.getElementById('yearSelect').value = currentDate.getFullYear();

    // Thêm sự kiện cho tháng và năm
    const monthSelect = document.getElementById('monthSelect');
    const yearSelect = document.getElementById('yearSelect');

    monthSelect.addEventListener('change', () => {
        updateBookingStats(bookings, monthSelect.value, yearSelect.value);
    });

    yearSelect.addEventListener('change', () => {
        updateBookingStats(bookings, monthSelect.value, yearSelect.value);
    });

    // Hiển thị thống kê cho tháng và năm mặc định
    updateBookingStats(bookings, currentDate.getMonth() + 1, currentDate.getFullYear());
}

function generateMonthOptions() {
    let options = '';
    for (let i = 1; i <= 12; i++) {
        options += `<option value="${i}">${i}</option>`;
    }
    return options;
}

function generateYearOptions() {
    const currentYear = new Date().getFullYear();
    let options = '';
    for (let i = 2022; i <= currentYear; i++) {
        options += `<option value="${i}">${i}</option>`;
    }
    return options;
}

function updateBookingStats(bookings, selectedMonth, selectedYear) {
    const monthlyCounts = {};

    bookings.forEach(booking => {
        const date = new Date(booking.bookingDate);
        const day = date.getDate();
        const month = date.getMonth() + 1;
        const year = date.getFullYear();

        if (month === parseInt(selectedMonth) && year === parseInt(selectedYear)) {
            const monthKey = `${year}-${month}`;
            if (!monthlyCounts[monthKey]) {
                monthlyCounts[monthKey] = {};
            }
            if (!monthlyCounts[monthKey][day]) {
                monthlyCounts[monthKey][day] = {
                    pending: 0,
                    confirmed: 0,
                    completed: 0,
                    cancelled: 0
                };
            }
            monthlyCounts[monthKey][day][booking.status]++;
        }
    });

    const monthKey = `${selectedYear}-${selectedMonth}`;
    const daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();

    const dailyStatsElement = document.getElementById('dailyStats');
    dailyStatsElement.innerHTML = '';

    const calendarDiv = document.createElement('div');
    calendarDiv.className = 'calendar';

    for (let day = 1; day <= daysInMonth; day++) {
        const counts = monthlyCounts[monthKey] && monthlyCounts[monthKey][day] ? monthlyCounts[monthKey][day] : { pending: 0, confirmed: 0, completed: 0, cancelled: 0 };
        const dayDiv = document.createElement('div');
        dayDiv.className = 'calendar-day';
        dayDiv.innerHTML = `
            <strong>${day}</strong>
            <div class="status-counts">
                ${counts.pending > 0 ? `<span class="status-count pending" style="color: orange;">${counts.pending}</span>` : ''}
                ${counts.confirmed > 0 ? `<span class="status-count confirmed" style="color: blue;">${counts.confirmed}</span>` : ''}
                ${counts.completed > 0 ? `<span class="status-count completed" style="color: green;">${counts.completed}</span>` : ''}
                ${counts.cancelled > 0 ? `<span class="status-count cancelled" style="color: red;">${counts.cancelled}</span>` : ''}
            </div>
        `;
        calendarDiv.appendChild(dayDiv);
    }

    dailyStatsElement.appendChild(calendarDiv);
}

// CSS cho lịch
const style = document.createElement('style');
style.innerHTML = `
    .modal-lg {
        max-width: 80%; /* Kích thước modal lớn hơn */
    }
    .calendar {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        gap: 15px;
        padding: 10px;
    }
    .calendar-day {
        border: 1px solid #ccc;
        padding: 10px;
        text-align: center;
        border-radius: 5px;
        height: 120px; /* Chiều cao lớn hơn */
    }
    .calendar-day strong {
        display: block;
        margin-bottom: 10px;
        font-size: 24px; /* Kích thước chữ ngày lớn hơn */
    }
    .status-counts {
        display: flex;
        justify-content: space-around;
    }
    .status-count {
        font-weight: bold;
        font-size: 16px; /* Kích thước chữ cho số lượng */
    }
    .status-legend {
        font-size: 14px; /* Kích thước chữ cho chú thích màu sắc */
    }
`;
document.head.appendChild(style);
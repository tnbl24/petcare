let currentBookingPage = 0; // Trang bắt đầu từ 0 cho API
const size = 10; // Kích thước trang
let servicesMap = {};
let usersMap = {};
let bookingsCache = [];
let searchTerm = '';
let selectedServiceId = ''; // ID dịch vụ đã chọn
let selectedStatus = ''; // Trạng thái đã chọn
let selectedDate = ''; // Ngày đã chọn

document.addEventListener("DOMContentLoaded", function () {
    loadServices(); // Bắt đầu tải dịch vụ

    document.getElementById('filterButtonSearch').addEventListener('click', function (event) {
        event.preventDefault();
        searchTerm = document.querySelector('.dataTable-input').value.trim();
        loadBookings(currentBookingPage); // Tải đặt phòng với từ khóa tìm kiếm
    });

    document.querySelector('.buttons a.btn-outline-dark').addEventListener('click', filterBookings);
});

function filterBookings(event) {
    if (event) {
        event.preventDefault();
    }

    selectedServiceId = document.getElementById('serviceSelect').value;
    selectedStatus = document.querySelectorAll('.filter-table select')[1].value;
    selectedDate = document.querySelector('.datepicker').value;

    loadBookings(currentBookingPage);
}

function loadBookings(page = 0) {
    const searchRequestDTO = {
        searchRequestDTO: [],
        globalOperator: "AND"
    };

    if (searchTerm) {
        const isNumber = !isNaN(searchTerm) && !isNaN(parseFloat(searchTerm));

        if (isNumber) {
            searchRequestDTO.searchRequestDTO.push({
                column: "id",
                value: parseInt(searchTerm, 10),
                operation: "EQUAL"
            });
        }

        searchRequestDTO.searchRequestDTO.push(
            {
                column: "fullName",
                value: searchTerm,
                operation: "JOIN",
                joinTable: "user"
            },
            {
                column: "phone",
                value: searchTerm,
                operation: "JOIN",
                joinTable: "user"
            }
        );

        searchRequestDTO.globalOperator = "OR";
    }

    if (selectedServiceId && selectedServiceId !== 'All') {
        searchRequestDTO.searchRequestDTO.push({
            column: "Id",
            value: selectedServiceId,
            operation: "JOIN",
            joinTable: "services"
        });
    }

    if (selectedStatus && selectedStatus !== 'All') {
        searchRequestDTO.searchRequestDTO.push({
            column: "status",
            value: selectedStatus,
            operation: "LIKE"
        });
    }

    if (selectedDate) {
        try {
            const parts = selectedDate.split("/");
            if (parts.length !== 3) {
                throw new Error("Định dạng ngày không hợp lệ");
            }

            const day = parseInt(parts[0], 10);
            const month = parseInt(parts[1], 10) - 1; // Tháng từ 0 đến 11
            const year = parseInt(parts[2], 10);
            const bookingDate = new Date(year, month, day);

            const formattedDate = `${day.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/${year}`;

            searchRequestDTO.searchRequestDTO.push({
                column: "bookingDate",
                value: formattedDate, // Sử dụng định dạng dd/MM/yyyy
                operation: "EQUAL"
            });

        } catch (error) {
            console.error("Lỗi khi chuyển đổi ngày:", error.message);
        }
    }

    console.log(searchTerm)
    // console.log('Điều kiện lọc đã được thêm:', searchRequestDTO.searchRequestDTO);
    console.log(searchRequestDTO);

    fetch(`/admin/booked-service-ctrl/filter/page/${page}?size=${size}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequestDTO)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            populateBookingTable(data.content); // Hiển thị các đặt phòng
            updateBookingPagination(data.totalPages); // Cập nhật phân trang
            bookingsCache = data.content;
            console.log('Bookings Cache:', bookingsCache);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}

function loadServices() {
    fetch('/admin/service-ctrl/active')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            servicesMap = data.reduce((map, service) => {
                map[service.id] = service.name;
                return map;
            }, {});
            populateServiceDropdown(); // Gọi hàm để cập nhật dropdown
            loadUsers(); // Tải người dùng sau khi dịch vụ đã được tải
            initServiceSelect()
        })
        .catch(error => console.error('There was a problem with the fetch operation:', error));
}
function loadUsers() {
    fetch('/admin/user-account-ctrl/all')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            usersMap = data.reduce((map, user) => {
                map[user.id] = {fullName: user.fullName, phone: user.phone,email: user.email};
                return map;
            }, {});
            loadBookings(currentBookingPage); // Tải đặt phòng sau khi người dùng đã được tải
        })
        .catch(error => console.error('There was a problem with the fetch operation:', error));
}

function updateBookingPagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    const maxVisiblePages = 5; // Số trang tối đa hiển thị

    // Tính toán startPage và endPage
    let startPage = Math.max(0, currentBookingPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    // Điều chỉnh startPage nếu endPage là trang cuối
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - (maxVisiblePages - 1));
    }

    // Tạo liên kết trang trước
    const prevLink = document.createElement('li');
    prevLink.className = 'page-item' + (currentBookingPage === 0 ? ' disabled' : '');
    const prevAnchor = document.createElement('a');
    prevAnchor.href = '#';
    prevAnchor.className = 'page-link';
    prevAnchor.textContent = '‹';
    prevAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentBookingPage > 0) {
            currentBookingPage--;
            loadBookings(currentBookingPage);
        }
    });
    prevLink.appendChild(prevAnchor);
    pagination.appendChild(prevLink);

    // Tạo các liên kết trang
    for (let i = startPage; i <= endPage; i++) {
        const link = document.createElement('li');
        link.className = 'page-item' + (i === currentBookingPage ? ' active' : '');
        const anchor = document.createElement('a');
        anchor.href = '#';
        anchor.className = 'page-link';
        anchor.textContent = i + 1;

        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            currentBookingPage = i;
            loadBookings(currentBookingPage);
        });

        link.appendChild(anchor);
        pagination.appendChild(link);
    }

    // Tạo liên kết trang sau
    const nextLink = document.createElement('li');
    nextLink.className = 'page-item' + (currentBookingPage === totalPages - 1 ? ' disabled' : '');
    const nextAnchor = document.createElement('a');
    nextAnchor.href = '#';
    nextAnchor.className = 'page-link';
    nextAnchor.textContent = '›';
    nextAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentBookingPage < totalPages - 1) {
            currentBookingPage++;
            loadBookings(currentBookingPage);
        }
    });
    nextLink.appendChild(nextAnchor);
    pagination.appendChild(nextLink);
}
function populateServiceDropdown() {
    const serviceSelect = document.getElementById('serviceSelect'); // Chọn dropdown
    serviceSelect.innerHTML = '<option selected="">All</option>'; // Xóa các tùy chọn cũ và thêm tùy chọn mặc định

    Object.entries(servicesMap).forEach(([id, name]) => {
        const option = document.createElement('option');
        option.value = id; // Giá trị là ID dịch vụ
        option.textContent = name; // Văn bản hiển thị là tên dịch vụ
        serviceSelect.appendChild(option); // Thêm tùy chọn vào dropdown
    });
}
function populateBookingTable(bookings) {
    const tableBody = document.getElementById('bookingTableBody');
    tableBody.innerHTML = '';

    bookings.forEach((booking) => {
        const statusBadge = booking.status === 'confirmed'
            ? '<span class="badge bg-info">Đã xác nhận</span>'
            : booking.status === 'completed'
                ? '<span class="badge bg-success">Hoàn thành</span>'
                : booking.status === 'cancelled'
                    ? '<span class="badge bg-danger">Đã hủy</span>'
                    : '<span class="badge bg-warning">Chờ xác nhận</span>';

        const row = document.createElement('tr');

        const bookingDate = new Date(booking.bookingDate); // Giả sử bookingDate là chuỗi ISO 8601
        const formattedTime = booking.bookingTime ? booking.bookingTime : 'Chưa xác định'; // Kiểm tra nếu có giá trị thời gian
        const formattedDate = bookingDate.toLocaleDateString('vi-VN');

        const createdAt = new Date(booking.createAt); // Giả sử createAt là chuỗi ISO 8601
        let formattedCreatedAt = createdAt.toLocaleString('vi-VN');

        const userInfo = usersMap[booking.userId] || { fullName: 'Không có tên', phone: 'Không có số điện thoại' };

        row.innerHTML = `
            <td>${booking.id || ''}</td>
            <td>${userInfo.fullName || ''}</td> 
            <td class="text-center">${formatPhoneNumber(userInfo.phone) || ''}</td>
            <td>${servicesMap[booking.serviceId] || ''}</td>
            <td class="text-center">${formattedTime || ''}</td>
            <td class="text-center">${formattedDate || ''}</td>
            <td class="text-center">${formattedCreatedAt || ''}</td>
            <td class="text-center">${formatPrice(booking.price) || ''}</td>
            <td class="text-center">${statusBadge || ''}</td>
            <td class="text-center">
                <a href="#" title="View Details" data-toggle="modal" data-target="#view-booked-service"
                style="display: inline-block; padding: 10px; margin-left: 10px; text-decoration: none; color: #000; text-align: center;">
                    <i class="fa fa-eye fa-lg" aria-hidden="true"></i>
                </a>
                <a href="#" title="Edit" data-toggle="modal" data-target="#edit-booked-service" onclick="loadEditBooking(${booking.id})">
                    <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg"></i>
                </a>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function formatPrice(price) {
    // Định dạng giá tiền với dấu chấm
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}
function formatPhoneNumber(phone) {
    // Giả sử số điện thoại có định dạng chuẩn và có thể chia thành các phần
    // Ví dụ: "0987654321" thành "098 765 4321"
    return phone.replace(/(\d{3})(\d{3})(\d+)/, '$1 $2 $3');
}

function loadBookingDetails(bookingId) {
    const booking = bookingsCache.find(b => b.id === bookingId);

    if (!booking) {
        console.error('Booking not found:', bookingId);
        return;
    }

    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(1) .col-8').textContent = booking.id;

    const createdAt = new Date(booking.createAt);
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(2) .col-8').textContent = createdAt.toLocaleString('vi-VN');

    fetch(`/admin/user-account-ctrl/${booking.userId}`)
        .then(response => {
            if (!response.ok) throw new Error('Không thể lấy thông tin người dùng');
            return response.json();
        })
        .then(data => {
            updateUserInfo(data, booking);
            loadPetById(booking.petId);
        })
        .catch(error => {
            console.error('Lỗi:', error);
            showAlert('Có lỗi xảy ra khi lấy thông tin người dùng.', 'alert-light-danger');
        });
}
function loadPetById(petId) {
    fetch(`/admin/pet-ctrl/${petId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể lấy thông tin thú cưng');
            }
            return response.json();
        })
        .then(pet => {
            updateUserPets(pet); // Truyền trực tiếp đối tượng thú cưng
        })
        .catch(error => {
            console.error('Lỗi:', error);
            showAlert('Có lỗi xảy ra khi lấy thông tin thú cưng.', 'alert-light-danger');
        });
}
function updateUserInfo(data, booking) {
    console.log(data)
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(3) .col-8').textContent = data.fullName || '';
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(4) .col-8').textContent = formatPhoneNumber(data.phone) || '';
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(5) .col-8').textContent = data.email || '';
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(6) .col-8').textContent = servicesMap[booking.serviceId] || '';

    const bookingDate = new Date(booking.bookingDate);
    const bookingTime = booking.bookingTime.split(':');

    bookingDate.setHours(parseInt(bookingTime[0], 10));
    bookingDate.setMinutes(parseInt(bookingTime[1], 10));

    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(7) .col-8').textContent = bookingDate.toLocaleTimeString('vi-VN');
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(8) .col-8').textContent = bookingDate.toLocaleDateString('vi-VN');
    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(9) .col-8').textContent = booking.note || '';

    const paymentMethodElement = document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(10) .col-8');
    const paymentMethod = booking.paymentMethod || '';

    if (paymentMethod === 'online') {
        paymentMethodElement.textContent = 'Thanh toán online';
    } else if (paymentMethod === 'offline') {
        paymentMethodElement.textContent = 'Trả tiền mặt';
    } else {
        paymentMethodElement.textContent = '';
    }

    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(11) .col-8').textContent = formatPrice(booking.price) + ' VNĐ';

    const statusBadge = booking.status === 'confirmed'
        ? '<span class="badge bg-info">Đã xác nhận</span>'
        : booking.status === 'completed'
            ? '<span class="badge bg-success">Hoàn thành</span>'
            : booking.status === 'cancelled'
                ? '<span class="badge bg-danger">Đã hủy</span>'
                : '<span class="badge bg-warning">Chờ xác nhận</span>';

    document.querySelector('#view-booked-service .modal-body .list-group-item:nth-child(12) .col-8').innerHTML = statusBadge;
}
function updateUserPets(pet) {
    const petsContainer = document.getElementById('userPets');
    petsContainer.innerHTML = ''; // Xóa nội dung cũ

    if (pet) {
        const petImage = pet.image ? `data:image/jpeg;base64,${pet.image}` : '/admin/assets/images/logoPetCare.svg';
        petsContainer.innerHTML += `
            <div class="pet">
                <img src="${petImage}" class="card-img-top img-fluid" alt="${pet.name}">
                <div class="card-body">
                    <h5 class="card-title">${pet.name || ''}</h5>
                    <p>Tuổi: ${pet.age || ''}</p>
                    <p>Giống: ${pet.type || ''}</p>
                    <p>Cân nặng: ${pet.weight || ''} kg</p>
                </div>
            </div>
        `;
    } else {
        petsContainer.innerHTML = '<p>Không có thông tin thú cưng.</p>';
    }
}
document.addEventListener('click', function (event) {
    if (event.target.closest('[data-toggle="modal"][data-target="#view-booked-service"]')) {
        const bookingId = parseInt(event.target.closest('tr').querySelector('td:first-child').textContent, 10); // Lấy ID từ hàng
        loadBookingDetails(bookingId);
    }
});
function loadEditBooking(bookingId) {
    const booking = bookingsCache.find(b => b.id === bookingId);
    if (!booking) return;

    document.getElementById('bookingId').value = booking.id;
    document.getElementById('customerId').value = booking.userId;
    document.getElementById('customerName').value = usersMap[booking.userId]?.fullName || '';
    document.getElementById('customerPhone').value = usersMap[booking.userId]?.phone || '';
    document.getElementById('customerEmail').value = usersMap[booking.userId]?.email || '';
    $('#serviceSelect2').val(booking.serviceId).trigger('change');

    const bookingTime = booking.bookingTime.split(':').slice(0, 2).join(':');
    console.log('Giờ đặt:', bookingTime); // Kiểm tra giá trị giờ đặt
    document.getElementById('bookingTimeSelect').value = bookingTime;

    if (Array.isArray(booking.bookingDate) && booking.bookingDate.length === 3) {
        const [year, month, day] = booking.bookingDate; // Giả sử định dạng là [YYYY, MM, DD]
        document.getElementById('bookingDate').value = `${String(day).padStart(2, '0')}/${String(month).padStart(2, '0')}/${year}`; // Định dạng lại
    } else if (typeof booking.bookingDate === 'string') {
        const [year, month, day] = booking.bookingDate.split('-');
        document.getElementById('bookingDate').value = `${day}/${month}/${year}`;
    } else if (booking.bookingDate instanceof Date) {
        const day = String(booking.bookingDate.getDate()).padStart(2, '0');
        const month = String(booking.bookingDate.getMonth() + 1).padStart(2, '0');
        const year = booking.bookingDate.getFullYear();
        document.getElementById('bookingDate').value = `${day}/${month}/${year}`;
    } else {
        console.error('Ngày không hợp lệ:', booking.bookingDate);
        return;
    }

    document.getElementById('bookingPrice').value = booking.price || '';

    document.getElementById('statusSelect').value = booking.status;
    $('#edit-booked-service').modal('show');
}
function updatebookservice() {
    const bookingId = document.getElementById('bookingId').value;
    const customerId = document.getElementById('customerId').value;

    const bookingTimeInput = document.getElementById('bookingTimeSelect').value;
    const bookingTime = bookingTimeInput ? bookingTimeInput + ":00" : null;

    const bookingDateInput = document.getElementById('bookingDate').value;

    let bookingDate;
    if (bookingDateInput) {
        const [day, month, year] = bookingDateInput.split('/');
        if (day && month && year) {
            const formattedDay = day.padStart(2, '0');
            const formattedMonth = month.padStart(2, '0');
            bookingDate = `${year}-${formattedMonth}-${formattedDay}`;
            console.log('Ngày đã định dạng:', bookingDate);
        } else {
            console.error('Ngày không hợp lệ:', bookingDateInput);
            return;
        }
    } else {
        console.error('Ngày không được cung cấp');
        return;
    }

    const bookingPrice = document.getElementById('bookingPrice').value;

    const updatedBooking = {
        id: bookingId,
        serviceId: $('#serviceSelect2').val(),
        bookingTime: bookingTime,
        bookingDate: bookingDate,
        status: document.getElementById('statusSelect').value,
        price: parseFloat(bookingPrice) || 0
    };

    const updatedCustomer = {
        id: customerId,
        fullName: document.getElementById('customerName').value,
        phone: document.getElementById('customerPhone').value,
        email: document.getElementById('customerEmail').value,
    };

    Promise.all([
        fetch(`/admin/booked-service-ctrl/${bookingId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedBooking),
        }),
        fetch(`/admin/user-account-ctrl/${customerId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(updatedCustomer),
        })
    ])
        .then(responses => {
            return Promise.all(responses.map(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(`Cập nhật thất bại: ${response.status} - ${response.statusText}. Chi tiết: ${err.message || JSON.stringify(err)}`);
                    });
                }
                return response.json();
            }));
        })
        .then(data => {
            $('#edit-booked-service').modal('hide'); // Đóng modal
            showAlert('Cập nhật thông tin thành công!', 'alert-light-success'); // Thông báo thành công
            loadBookings(currentBookingPage); // Tải lại danh sách đặt phòng
        })
        .catch(error => {
            console.error('Có lỗi xảy ra:', error.message);
            // showAlert('Có lỗi xảy ra khi cập nhật: ' + error.message, 'alert-light-danger');
            $('#edit-booked-service').modal('hide'); // Đóng modal

            showAlert('Cập nhật thông tin thành công!', 'alert-light-success'); // Thông báo thành công
            loadBookings(currentBookingPage); // Tải lại danh sách đặt phòng


        });
}
function initServiceSelect() {
    const serviceSelect = $('#serviceSelect2');
    serviceSelect.empty(); // Xóa các tùy chọn cũ
    serviceSelect.append('<option selected="">Choose...</option>');

    Object.entries(servicesMap).forEach(([id, name]) => {
        const option = new Option(name, id, false, false);
        serviceSelect.append(option);
    });

    serviceSelect.select2();
}



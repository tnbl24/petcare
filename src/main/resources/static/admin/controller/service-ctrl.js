let searchTerm = '';
let currentPage = 0;
let groupServices = [];
const pageSize = 10;
loadServiceTable();
document.addEventListener("DOMContentLoaded", function () {
    fetchGroupServices();

    const searchInput = document.querySelector('.dataTable-input'); // Tìm kiếm
    const filterButton = document.getElementById('filterButton');
    const serviceTypeSelect = document.getElementById('serviceTypeSelect');

    if (searchInput) {
        searchInput.addEventListener('input', debounce(function () {
            searchTerm = this.value.trim();
            currentPage = 0; // Đặt lại về trang đầu khi tìm kiếm mới
            console.log(`Đang tìm kiếm: "${searchTerm}" trên trang ${currentPage}`);
        }, 300));
    } else {
        console.error('Không tìm thấy phần tử tìm kiếm.');
    }

    filterButton.addEventListener('click', function (event) {
        event.preventDefault(); // Ngăn hành vi mặc định

        const selectedServiceType = serviceTypeSelect.value; // ID của nhóm dịch vụ
        console.log(`Đã chọn loại dịch vụ: ${selectedServiceType}`);

        currentPage = 0; // Đặt lại trang về 0 khi lọc
        loadServiceTable(currentPage, '', selectedServiceType); // Gọi hàm tải với tham số đã cập nhật
    });

    const filterButtonSearch = document.getElementById('filterButtonSearch');
    filterButtonSearch.addEventListener('click', function (event) {
        event.preventDefault(); // Ngăn hành vi mặc định
        currentPage = 0; // Đặt lại trang về 0 khi tìm kiếm
        loadServiceTable(currentPage, searchTerm); // Gọi hàm tải với từ khóa tìm kiếm
    });

    loadServiceTable(currentPage, searchTerm); // Tải bảng với từ khóa tìm kiếm ban đầu
});

function debounce(func, delay) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

function populateGroupServiceDropdown() {
    const groupServiceSelect = document.querySelector('.form-select'); // Chọn dropdown nhóm dịch vụ
    groupServiceSelect.innerHTML = '<option selected="">All</option>'; // Xóa các tùy chọn cũ và thêm tùy chọn mặc định

    groupServices.forEach(group => {
        const option = document.createElement('option');
        option.value = group.id; // Giá trị là ID nhóm dịch vụ
        option.textContent = group.name; // Văn bản hiển thị là tên nhóm dịch vụ
        groupServiceSelect.appendChild(option); // Thêm tùy chọn vào dropdown
    });
}

function fetchGroupServices() {
    fetch('/admin/group-service-ctrl')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            groupServices = data;
            populateGroupServiceDropdown();
            loadServiceTable(currentPage, searchTerm);
        })
        .catch(error => {
            console.error('Error fetching group services:', error);
        });
}
function loadServiceTable(page = 0, search = '', groupServiceId = '') {
    const searchRequestDTO = {
        searchRequestDTO: [
            {
                column: "name",
                value: '',
                operation: "LIKE"
            }
        ],
        globalOperator: "OR"
    };

    if (search) {
        searchRequestDTO.searchRequestDTO=[]
        searchRequestDTO.searchRequestDTO.push(
            {
                column: "name",
                value: search,
                operation: "LIKE"
            },
            {
                column: "description",
                value: search,
                operation: "LIKE"
            }
        );
    }

    if (groupServiceId && groupServiceId !== 'All') {
        searchRequestDTO.searchRequestDTO.push({
            column: "Id",
            value: groupServiceId,
            operation: "JOIN",
            joinTable: "groupService"
        });

        searchRequestDTO.globalOperator = "AND"; // Đặt globalOperator thành AND
        console.log('Điều kiện lọc đã được thêm:', searchRequestDTO.searchRequestDTO);
    }else if (groupServiceId && groupServiceId == 'All') {
        searchRequestDTO.searchRequestDTO.push({
            column: "name",
            value: '',
            operation: "LIKE"
        });
        searchRequestDTO.globalOperator = "OR"; // Đặt globalOperator thành AND
        console.log('Điều kiện lọc đã được thêm:', searchRequestDTO.searchRequestDTO);
    }
    console.log('Yêu cầu gửi đi:', JSON.stringify(searchRequestDTO, null, 2));

    fetch(`/admin/service-ctrl/filter/page/${page}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequestDTO)
    })
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            console.log('Dữ liệu nhận được:', data); // In dữ liệu ra console
            populateServiceTable(data.content);
            updateServicePagination(data.totalPages);
        })
        .catch(error => console.error('Fetch error:', error));
}

function updateServicePagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    const maxVisiblePages = 5; // Số trang tối đa hiển thị
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - (maxVisiblePages - 1));
    }

    // Tạo liên kết trang trước
    const prevLink = document.createElement('li');
    prevLink.className = 'page-item' + (currentPage === 0 ? ' disabled' : '');
    const prevAnchor = document.createElement('a');
    prevAnchor.href = '#';
    prevAnchor.className = 'page-link';
    prevAnchor.textContent = '‹';
    prevAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentPage > 0) {
            currentPage--;
            loadServiceTable(currentPage, searchTerm, serviceTypeSelect.value);
        }
    });
    prevLink.appendChild(prevAnchor);
    pagination.appendChild(prevLink);

    // Tạo liên kết các trang
    for (let i = startPage; i <= endPage; i++) {
        const link = document.createElement('li');
        link.className = 'page-item' + (i === currentPage ? ' active' : '');
        const anchor = document.createElement('a');
        anchor.href = '#';
        anchor.className = 'page-link';
        anchor.textContent = i + 1;

        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            currentPage = i;
            loadServiceTable(currentPage, searchTerm, serviceTypeSelect.value);
        });

        link.appendChild(anchor);
        pagination.appendChild(link);
    }

    // Tạo liên kết trang sau
    const nextLink = document.createElement('li');
    nextLink.className = 'page-item' + (currentPage === totalPages - 1 ? ' disabled' : '');
    const nextAnchor = document.createElement('a');
    nextAnchor.href = '#';
    nextAnchor.className = 'page-link';
    nextAnchor.textContent = '›';
    nextAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadServiceTable(currentPage, searchTerm, serviceTypeSelect.value);
        }
    });
    nextLink.appendChild(nextAnchor);
    pagination.appendChild(nextLink);
}

function populateServiceTable(services) {
    const tableBody = document.getElementById('serviceBody');
    tableBody.innerHTML = '';

    services.forEach((service, index) => {
        const row = document.createElement('tr');

        const groupService = groupServices.find(group => group.id === service.groupServiceId);
        const groupName = groupService ? groupService.name : 'Không xác định';

        let imageSrc = '';
        if (typeof service.image === 'string' && service.image.startsWith('/9j/')) {
            imageSrc = `data:image/jpeg;base64,${service.image}`;
        } else {
            console.error('Invalid image data for service:', service);
            imageSrc = 'path/to/default/image.png';
        }

        const priceDisplay = service.price ? formatPrice(service.price) : '';

        const isChecked = service.hasDetail === '1'; // Kiểm tra hasDetail
        const checkboxDisplay = `
            <div class="form-check">
                <div class="custom-checkbox">
                    <input type="checkbox" class="form-check-input form-check-success" ${isChecked ? 'checked' : ''} disabled>
                </div>
            </div>
        `;

        row.innerHTML = `
            <td class="text-center">${index + 1 + (currentPage * pageSize)}</td>
            <td>${service.name || ''}</td>
            <td>${groupName || ''}</td>
            <td>${service.description || ''}</td>
            <td class="text-center">
                ${service.image && service.image.trim() !== '' ? `<img src="${imageSrc}" alt="${service.name}" style="width: 100px; height: 100px;">` : ''}
            </td>
            <td class="text-center">${priceDisplay}</td>
            <td class="text-center">${checkboxDisplay}</td>
            <td class="text-center">
                <a href="#" title="Chỉnh sửa"
                   data-toggle="modal"
                   data-target="#edit-service"
                   onclick="prepareEditService(${service.id}, '${service.name}', ${service.groupServiceId}, '${service.description}', '${service.image}', ${service.price}, '${service.hasDetail}')">
                   <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg" style="margin-right: 5px"></i>
                </a>
                <a href="#" title="Xóa" style="margin-left: 10px;"
                   data-toggle="modal"
                   data-target="#delete-service"
                   onclick="openDeleteServiceModal(${service.id}, '${service.name}')">
                    <i class="fas fa-trash badge-circle badge-circle-light-secondary font-medium-1 delete-icon fa-lg"></i>
                </a>
            </td>
        `;
        tableBody.appendChild(row);
    });
}
function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ') + ''; // Chỉnh sửa để hiển thị đơn vị
}

let serviceIdToDelete;

function openDeleteServiceModal(serviceId, serviceName) {
    serviceIdToDelete = serviceId; // Lưu ID dịch vụ cần xóa
    document.getElementById('serviceNameToDelete').textContent = serviceName; // Hiển thị tên dịch vụ
    $('#delete-service').modal('show'); // Hiển thị modal
}
function deleteService() {
    console.log('Xóa dịch vụ với ID:', serviceIdToDelete); // Kiểm tra ID dịch vụ
    fetch(`/admin/service-ctrl/${serviceIdToDelete}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Response:', response); // Ghi lại phản hồi
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            // Xử lý phản hồi không phải JSON
            return response.text(); // Chuyển sang text thay vì json
        })
        .then(text => {
            console.log('Phản hồi từ server:', text); // Ghi lại phản hồi
            $('#delete-service').modal('hide'); // Đóng modal
            showAlert('Xóa dịch vụ thành công!', 'alert-light-success');
            loadServices(); // Cập nhật lại danh sách dịch vụ

        })
        .catch(error => {
            console.error('Có lỗi xảy ra khi xóa dịch vụ:', error);
            showAlert('Xóa dịch vụ thất bại!', 'alert-light-danger');
        });
}
function previewImage(event) {
    const imagePreview = document.querySelector('#edit-service .image-preview');
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = 'block';
        };

        reader.readAsDataURL(file);
    } else {
        imagePreview.style.display = 'none';
    }
}

let currentServiceId = null;

function prepareEditService(id, name, groupServiceId, description, image, price, hasDetail) {
    currentServiceId = id;
    document.querySelector('#edit-service input[name="name"]').value = name;
    document.querySelector('#edit-service textarea[name="descriptionEdit"]').value = description;
    document.getElementById('editServicePrice').value = price;
    document.getElementById('editServiceHasDetail').checked = (hasDetail === '1');

    const imagePreview = document.querySelector('#edit-service .image-preview');

    // Kiểm tra giá trị của image
    if (image && image !== 'null') {
        imagePreview.src = `data:image/jpeg;base64,${image}`;
        imagePreview.alt = '';
        imagePreview.style.display = 'block'; // Hiện hình ảnh
    } else {
        imagePreview.src = ''; // Đặt src rỗng
        imagePreview.alt = ''; // Đặt alt rỗng
        imagePreview.style.display = 'none'; // Ẩn hình ảnh
    }

    loadServiceGroupsForEdit(groupServiceId);
}

function loadServiceGroupsForEdit(selectedGroupId) {
    fetch('/admin/group-service-ctrl')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(groups => {
            const groupSelect = document.querySelector('#edit-service .form-select');
            groupSelect.innerHTML = '<option selected="">Choose...</option>';

            groups.forEach(group => {
                const option = document.createElement('option');
                option.value = group.id;
                option.textContent = group.name;
                groupSelect.appendChild(option);
            });

            if (selectedGroupId) {
                groupSelect.value = selectedGroupId;
            }
        })
        .catch(error => console.error('Error fetching service groups:', error));
}

function updateService() {
    const serviceName = document.querySelector('#edit-service input[name="name"]').value.trim();
    const groupId = document.querySelector('#edit-service select[name="groupServiceId"]').value;
    const description = document.querySelector('#edit-service textarea[name="descriptionEdit"]').value.trim();
    const price = document.getElementById('editServicePrice').value; // Lấy giá từ trường nhập liệu
    const hasDetail = document.getElementById('editServiceHasDetail').checked ? '1' : '0'; // Lấy trạng thái hasDetail
    const imageFileInput = document.querySelector('#edit-service input[name="imageFile"]');

    if (!serviceName || groupId === "Choose..." || !description) {
        showAlert('Vui lòng điền đầy đủ thông tin!', 'alert-light-danger');
        return;
    }

    const formData = new FormData();
    formData.append('name', serviceName);
    formData.append('groupServiceId', groupId);
    formData.append('description', description);
    formData.append('price', price); // Thêm giá vào FormData
    formData.append('hasDetail', hasDetail); // Thêm trạng thái hasDetail vào FormData

    if (imageFileInput && imageFileInput.files.length > 0) {
        const file = imageFileInput.files[0];
        formData.append('imageFile', file);
    }

    sendUpdateRequest(formData);
}
function loadServices() {
    loadServiceTable(currentPage, searchTerm); // Tải bảng dịch vụ với trang và từ khóa tìm kiếm hiện tại
}
function sendUpdateRequest(formData) {
    for (const [key, value] of formData.entries()) {
        console.log(`${key}:`, value);
    }

    fetch(`/admin/service-ctrl/${currentServiceId}`, {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(`Cập nhật dịch vụ thất bại: ${text}`);
                });
            }
            $('#edit-service').modal('hide');
            showAlert('Cập nhật dịch vụ thành công!', 'alert-light-success');
            loadServices();
        })
        .catch(error => {
            console.error('Có lỗi xảy ra khi cập nhật dịch vụ:', error.message);
            showAlert(error.message, 'alert-light-danger');
        });
}

function previewImageCreate(event) {
    const imagePreview = document.querySelector('.image-preview-create'); // Thay đổi class ở đây
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            imagePreview.src = e.target.result; // Đặt nguồn hình ảnh preview
            imagePreview.style.display = 'block'; // Hiển thị hình ảnh
        }
        reader.readAsDataURL(file); // Đọc tệp hình ảnh
    } else {
        imagePreview.src = 'path/to/default/image.png'; // Đặt lại hình ảnh nếu không có tệp
        imagePreview.style.display = 'none'; // Ẩn hình ảnh nếu không có tệp
    }
}
function openAddServiceModal() {
    document.querySelector('#create-service input[name="name"]').value = '';
    document.querySelector('#create-service textarea[name="description"]').value = '';
    document.querySelector('#create-service select[name="groupServiceId"]').selectedIndex = 0; // Chọn tùy chọn đầu tiên
    document.getElementById('price').value = ''; // Đặt lại giá
    document.getElementById('customColorCheck1').checked = false; // Đặt trạng thái checkbox mặc định

    const imagePreview = document.querySelector('#create-service .image-preview-create');
    if (imagePreview) {
        imagePreview.style.display = 'none'; // Ẩn hình ảnh preview
        imagePreview.src = 'path/to/default/image.png'; // Đặt lại hình ảnh mặc định
    }

    fetch('/admin/group-service-ctrl')
        .then(response => {
            if (!response.ok) {
                throw new Error('Không thể tải danh mục dịch vụ.');
            }
            return response.json();
        })
        .then(data => {
            const groupServiceSelect = document.querySelector('#create-service select[name="groupServiceId"]');
            groupServiceSelect.innerHTML = ''; // Xóa các tùy chọn hiện tại

            // Thêm tùy chọn mặc định
            const defaultOption = document.createElement('option');
            defaultOption.text = 'Chọn loại dịch vụ';
            defaultOption.value = '';
            groupServiceSelect.add(defaultOption);

            // Thêm các tùy chọn từ dữ liệu nhận được
            data.forEach(group => {
                const option = document.createElement('option');
                option.text = group.name;
                option.value = group.id;
                groupServiceSelect.add(option);
            });
        })
        .catch(error => {
            console.error('Có lỗi xảy ra khi lấy danh mục:', error.message);
            showAlert(error.message, 'alert-light-danger'); // Hiển thị thông báo lỗi
        });

    $('#create-service').modal('show'); // Mở modal thêm mới
}
function addService() {
    const serviceName = document.querySelector('#create-service input[name="name"]').value.trim();
    const groupId = document.querySelector('#create-service select[name="groupServiceId"]').value;
    const description = document.querySelector('#create-service textarea[name="description"]').value.trim();
    const price = document.getElementById('price').value.trim();
    const hasDetail = document.getElementById('customColorCheck1').checked ? '1' : '0'; // Lấy trạng thái hasDetail
    const imageFileInput = document.querySelector('#create-service input[name="imageFile"]');

    if (!serviceName) {
        showAlert('Vui lòng điền tên dịch vụ!', 'alert-light-danger');
        return;
    }
    if (!groupId) {
        showAlert('Vui lòng chọn loại dịch vụ!', 'alert-light-danger');
        return;
    }


    const formData = new FormData();
    formData.append('name', serviceName);
    formData.append('groupServiceId', groupId);
    formData.append('description', description);
    formData.append('price', price); // Thêm giá vào FormData
    formData.append('hasDetail', hasDetail); // Thêm hasDetail vào FormData

    if (imageFileInput && imageFileInput.files.length > 0) {
        const file = imageFileInput.files[0];
        formData.append('imageFile', file);
    }

    fetch('/admin/service-ctrl', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Thêm dịch vụ thất bại');
                });
            }
            $('#create-service').modal('hide'); // Đóng modal
            showAlert('Thêm dịch vụ thành công!', 'alert-light-success');
            loadServices(); // Tải lại danh sách dịch vụ
        })
        .catch(error => {
            console.error('Có lỗi xảy ra khi thêm dịch vụ:', error.message);
            showAlert(error.message, 'alert-light-danger');
        });
}
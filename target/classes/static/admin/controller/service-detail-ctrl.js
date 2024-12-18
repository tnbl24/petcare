document.addEventListener("DOMContentLoaded", function () {
    loadServiceDetails();
});

let currentServiceDetailPage = 0;

function loadServiceDetails(page = 0) {
    const size = 10; // Kích thước trang
    fetch(`/admin/service-detail-ctrl/page/${page}?size=${size}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            populateServiceDetailTable(data.content);
            updateServiceDetailPagination(data.totalPages);
        })
        .catch(error => console.error('There was a problem with the fetch operation:', error));
}

function populateServiceDetailTable(serviceDetails) {
    const tableBody = document.getElementById('serviceDetailBody');
    tableBody.innerHTML = '';

    serviceDetails.forEach((service, index) => {
        console.log(service.weight)
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="text-center">${index + 1 || ''}</td>
            <td class="text-center">Từ ${service.weight || ''}kg</td>
            <td class="text-center">${formatPrice(service.price) || ''}</td>
            <td class="text-center">
                <a href="#" title="Edit" data-toggle="modal" data-target="#edit-service-detail" onclick="prepareEditServiceDetail(${service.id}, '${service.weight}', ${service.price})">
                    <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg" style="margin-right: 5px"></i>
                </a>
                <a href="#" title="Delete" style="margin-left: 10px;" data-toggle="modal" data-target="#delete-service-detail" onclick="openDeleteServiceDetailModal(${service.id},${service.weight})">
                    <i class="fas fa-trash badge-circle badge-circle-light-secondary font-medium-1 delete-icon fa-lg"></i>
                </a>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function updateServiceDetailPagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    const maxVisiblePages = 5;

    let startPage = Math.max(0, currentServiceDetailPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - (maxVisiblePages - 1));
    }

    const prevLink = document.createElement('li');
    prevLink.className = 'page-item' + (currentServiceDetailPage === 0 ? ' disabled' : '');
    const prevAnchor = document.createElement('a');
    prevAnchor.href = '#';
    prevAnchor.className = 'page-link';
    prevAnchor.textContent = '‹';
    prevAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentServiceDetailPage > 0) {
            currentServiceDetailPage--;
            loadServiceDetails(currentServiceDetailPage);
        }
    });
    prevLink.appendChild(prevAnchor);
    pagination.appendChild(prevLink);

    for (let i = startPage; i <= endPage; i++) {
        const link = document.createElement('li');
        link.className = 'page-item' + (i === currentServiceDetailPage ? ' active' : '');
        const anchor = document.createElement('a');
        anchor.href = '#';
        anchor.className = 'page-link';
        anchor.textContent = i + 1;

        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            currentServiceDetailPage = i;
            loadServiceDetails(currentServiceDetailPage);
        });

        link.appendChild(anchor);
        pagination.appendChild(link);
    }

    const nextLink = document.createElement('li');
    nextLink.className = 'page-item' + (currentServiceDetailPage === totalPages - 1 ? ' disabled' : '');
    const nextAnchor = document.createElement('a');
    nextAnchor.href = '#';
    nextAnchor.className = 'page-link';
    nextAnchor.textContent = '›';
    nextAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentServiceDetailPage < totalPages - 1) {
            currentServiceDetailPage++;
            loadServiceDetails(currentServiceDetailPage);
        }
    });
    nextLink.appendChild(nextAnchor);
    pagination.appendChild(nextLink);
}

loadServiceDetails(currentServiceDetailPage);

let serviceIdToDelete = null;

function openDeleteServiceDetailModal(id, weight) {
    serviceIdToDelete = id; // Giữ ID để xóa
    document.getElementById('serviceCodeToDelete').textContent = weight; // Hiển thị weight
    $('#delete-service-detail').modal('show');
}

function deleteServiceDetail() {
    if (serviceIdToDelete) {
        fetch(`/admin/service-detail-ctrl/${serviceIdToDelete}`, { method: 'DELETE' })
            .then(response => {
                if (!response.ok) throw new Error('Không thể xóa dịch vụ');
                $('#delete-service-detail').modal('hide');
                showAlert('Xóa dịch vụ thành công!', 'alert-light-success');
                loadServiceDetails(currentServiceDetailPage);
            })
            .catch(error => {
                console.error('Lỗi:', error);
                showAlert('Xóa dịch vụ thất bại!', 'alert-light-danger');
            });
    }
}

function updateServiceDetail() {
    const id = window.currentServiceDetailId;

    const weight = document.getElementById('weight').value;

    const price = document.getElementById('price').value.trim();

    if (!weight || !price) {
        showAlert('Vui lòng điền đầy đủ thông tin!', 'alert-light-danger');
        return;
    }
    console.log(weight)
    console.log(price)
    fetch(`/admin/service-detail-ctrl/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            weight: weight,
            price: parseFloat(price)
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Cập nhật dịch vụ thất bại');
            }
            return response.json();
        })
        .then(() => {
            $('#edit-service-detail').modal('hide');
            showAlert('Cập nhật dịch vụ thành công!', 'alert-light-success');
            loadServiceDetails(currentServiceDetailPage);
        })
        .catch(error => {
            console.error('Lỗi:', error);
            showAlert('Cập nhật dịch vụ thất bại: ' + error.message, 'alert-light-danger');
        });
}

function prepareEditServiceDetail(id, weight, price) {
    console.log(weight)
    // Ép kiểu weight thành chuỗi
    weight = String(weight);

    // Gán lại giá trị cho phần tử HTML bằng innerText hoặc textContent
    document.getElementById('weight').value = weight;
    document.getElementById('price').value = price;
    window.currentServiceDetailId = id;
}



function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
}

function createServiceDetail() {
    const weight = document.getElementById('newWeight').value; // Sử dụng ID đúng
    const price = document.getElementById('newPrice').value.trim();   // Sử dụng ID đúng

    if (!weight || !price) {
        showAlert('Vui lòng điền đầy đủ thông tin!', 'alert-light-danger');
        return;
    }

    fetch(`/admin/service-detail-ctrl`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            weight: weight,
            price: parseFloat(price)
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Tạo dịch vụ thất bại');
            }
            return response.json();
        })
        .then(() => {
            $('#create-service-detail').modal('hide');
            showAlert('Tạo dịch vụ thành công!', 'alert-light-success');
            document.getElementById('newWeight').value = ''; document.getElementById('newPrice').value = '';
            loadServiceDetails(currentServiceDetailPage);
        })
        .catch(error => {
            console.error('Lỗi:', error);
            showAlert('Tạo dịch vụ thất bại: ' + error.message, 'alert-light-danger');
        });
}


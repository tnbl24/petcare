document.addEventListener("DOMContentLoaded", function() {
    const searchInput = document.querySelector('.dataTable-input');
    if (searchInput) {
        searchInput.addEventListener('input', debounce(function () {
            searchTerm = this.value.trim();
            currentPage = 0;
            console.log(`Đang tìm kiếm: "${searchTerm}" trên trang ${currentPage}`);
            loadTable(currentPage, searchTerm);
        }, 300));
    } else {
        console.error('Không tìm thấy phần tử tìm kiếm.');
    }
});
let currentPage = 0;
let searchTerm = '';

loadTable(currentPage, searchTerm);

function debounce(func, delay) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

function loadTable(page = 0, search = '') {
    const size = 10;
    const searchRequestDTO = {
        searchRequestDTO: [
            {
                column: "name",
                value: search,
                operation: "LIKE"
            }
        ],
        globalOperator: "OR"
    };

    fetch(`/admin/group-service-ctrl/filter/page/${page}`, {
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
            populateTable(data.content);
            updatePagination(data.totalPages);
        })
        .catch(error => console.error('Fetch error:', error));
}

function populateTable(groupServices) {
    const tableBody = document.getElementById('groupServiceBody');
    tableBody.innerHTML = groupServices.map((service, index) => `
        <tr>
            <td class="text-center">${index + 1 + currentPage * 10}</td>
            <td>${service.name}</td>
            <td class="text-center">
                <a href="#" data-toggle="modal" data-target="#edit-group-service" 
                   onclick="prepareEdit(${service.id}, '${service.name}')">
                   <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg"
                                                   style="margin-right: 5px"></i>
                </a>
                <a href="#" style="margin-left: 10px;" onclick="openDeleteModal(${service.id}, '${service.name}')">
                 <i class="fas fa-trash badge-circle badge-circle-light-secondary font-medium-1 delete-icon fa-lg"></i>
                </a>
            </td>
        </tr>
    `).join('');
}

function updatePagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    const maxVisiblePages = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - (maxVisiblePages - 1));
    }

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
            loadTable(currentPage, searchTerm);
        }
    });
    prevLink.appendChild(prevAnchor);
    pagination.appendChild(prevLink);

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
            loadTable(currentPage, searchTerm);
        });

        link.appendChild(anchor);
        pagination.appendChild(link);
    }

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
            loadTable(currentPage, searchTerm);
        }
    });
    nextLink.appendChild(nextAnchor);
    pagination.appendChild(nextLink);
}


function prepareEdit(id, name) {
    document.getElementById('editGroupServiceId').value = id;
    document.getElementById('editGroupServiceName').value = name;
}

function createGroupService() {
    const name = document.getElementById('newGroupServiceName').value.trim(); // Trim để loại bỏ khoảng trắng

    // Kiểm tra nếu tên trống
    if (!name) {
        showAlert('Tên nhóm dịch vụ không được để trống!', 'alert-light-danger');
        return; // Dừng hàm nếu tên trống
    }

    fetch('/admin/group-service-ctrl', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name }),
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to create group service');
            return response.json();
        })
        .then(() => {
            $('#create-group-service').modal('hide');
            document.getElementById('newGroupServiceName').value = '';
            showAlert('Tạo nhóm dịch vụ thành công!', 'alert-light-success');
            loadTable();
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Tạo nhóm dịch vụ thất bại!' + error.message, 'alert-light-danger');
        });
}

function updateGroupService() {
    const id = document.getElementById('editGroupServiceId').value;
    const name = document.getElementById('editGroupServiceName').value;

    fetch(`/admin/group-service-ctrl/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name }),
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to update group service');
            return response.json();
        })
        .then(updatedService => {
            updateTableRow(updatedService);
            $('#edit-group-service').modal('hide');
            showAlert('Cập nhật nhóm dịch vụ thành công!', 'alert-light-success');
            loadTable();
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Cập nhật nhóm dịch vụ thất bại!' + error.message, 'alert-light-danger');
        });
}

function updateTableRow(updatedService) {
    const row = Array.from(document.getElementById('groupServiceBody').rows)
        .find(row => row.querySelector('a[data-id]')?.dataset.id == updatedService.id);

    if (row) {
        row.cells[1].textContent = updatedService.name;
    }
}

function openDeleteModal(id, name) {
    document.getElementById('groupServiceNameToDelete').textContent = name;
    document.getElementById('confirmDeleteButton').onclick = () => deleteGroupService(id);
    $('#delete-group-service').modal('show');
}

function deleteGroupService(id) {
    fetch(`/admin/group-service-ctrl/${id}`, { method: 'DELETE' })
        .then(response => {
            if (!response.ok) throw new Error('Failed to delete group service');
            $('#delete-group-service').modal('hide');
            showAlert('Xóa nhóm dịch vụ thành công!', 'alert-light-success');
            loadTable();
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Xóa nhóm dịch vụ thất bại! Có dịch vụ sử dụng loại dịch vụ này', 'alert-light-danger');
        });
}
let currentUserAccountPage = 0;
let searchTerm = '';

document.addEventListener("DOMContentLoaded", function () {
    loadUserAccounts(currentUserAccountPage);

    const searchInput = document.querySelector('.dataTable-input');

    if (searchInput) {
        searchInput.addEventListener('input', debounce(function () {
            searchTerm = this.value.trim();
            currentUserAccountPage = 0;
            loadUserAccounts(currentUserAccountPage, searchTerm);
        }, 300));
    } else {
        console.error('Không tìm thấy phần tử tìm kiếm.');
    }
});

function loadUserAccounts(page = 0, search = '') {
    const size = 10;
    const searchRequestDTO = {
        searchRequestDTO: [
            {
                column: "fullName",
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
            column: "fullName",
            value: search,
            operation: "LIKE"
        }
        ,{
            column: "phone",
                value: search,
                operation: "LIKE"
        },
        {
            column: "email",
                value: search,
            operation: "LIKE"
        },
            {
                column: "address",
                value: search,
                operation: "LIKE"
            }
        );
    }

    fetch(`/admin/user-account-ctrl/active/filter/page/${page}?size=${size}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequestDTO) // Chuyển đổi đối tượng thành JSON
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            populateUserAccountTable(data.content); // Cập nhật bảng
            updateUserAccountPagination(data.totalPages); // Cập nhật phân trang
        })
        .catch(error => console.error('There was a problem with the fetch operation:', error));
}

function updateUserAccountPagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = ''; // Xóa pagination cũ

    const maxVisiblePages = 5; // Số trang tối đa hiển thị
    let startPage = Math.max(0, currentUserAccountPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - (maxVisiblePages - 1));
    }

    const prevLink = document.createElement('li');
    prevLink.className = 'page-item' + (currentUserAccountPage === 0 ? ' disabled' : '');
    const prevAnchor = document.createElement('a');
    prevAnchor.href = '#';
    prevAnchor.className = 'page-link';
    prevAnchor.textContent = '‹';
    prevAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentUserAccountPage > 0) {
            currentUserAccountPage--;
            loadUserAccounts(currentUserAccountPage, searchTerm); // Gọi lại với từ khóa tìm kiếm
        }
    });
    prevLink.appendChild(prevAnchor);
    pagination.appendChild(prevLink);

    for (let i = startPage; i <= endPage; i++) {
        const link = document.createElement('li');
        link.className = 'page-item' + (i === currentUserAccountPage ? ' active' : '');
        const anchor = document.createElement('a');
        anchor.href = '#';
        anchor.className = 'page-link';
        anchor.textContent = i + 1;

        anchor.addEventListener('click', (event) => {
            event.preventDefault();
            currentUserAccountPage = i; // Cập nhật trang hiện tại
            loadUserAccounts(currentUserAccountPage, searchTerm); // Gọi lại với từ khóa tìm kiếm
        });

        link.appendChild(anchor);
        pagination.appendChild(link);
    }

    const nextLink = document.createElement('li');
    nextLink.className = 'page-item' + (currentUserAccountPage === totalPages - 1 ? ' disabled' : '');
    const nextAnchor = document.createElement('a');
    nextAnchor.href = '#';
    nextAnchor.className = 'page-link';
    nextAnchor.textContent = '›';
    nextAnchor.addEventListener('click', (event) => {
        event.preventDefault();
        if (currentUserAccountPage < totalPages - 1) {
            currentUserAccountPage++;
            loadUserAccounts(currentUserAccountPage, searchTerm); // Gọi lại với từ khóa tìm kiếm
        }
    });
    nextLink.appendChild(nextAnchor);
    pagination.appendChild(nextLink);
}

function debounce(func, delay) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}
function populateUserAccountTable(userAccounts) {
    const tableBody = document.getElementById('userAccountBody');
    tableBody.innerHTML = '';

    userAccounts.forEach((userAccount) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="text-center">${userAccount.id || ''}</td>
            <td>${userAccount.fullName || ''}</td>
            <td>${formatPhoneNumber(userAccount.phone) || ''}</td>
            <td>${userAccount.email || ''}</td>
            <td>${userAccount.address || ''}</td>
            <td class="text-center">
                <a href="#" title="Edit" data-toggle="modal" data-target="#edit-user-account" 
                   onclick="prepareEditUser('${userAccount.id}', '${userAccount.fullName}', '${userAccount.phone}', '${userAccount.email}', '${userAccount.address}')">
                    <i class="fas fa-pencil-alt badge-circle badge-circle-light-secondary font-medium-1 edit-icon fa-lg"
                       style="margin-right: 5px"></i>
                </a> 
            </td>
        `;
        tableBody.appendChild(row);
    });
}


let userIdToDelete = null;

function openDeleteUserModal(id, fullName) {
    userIdToDelete = id; // Lưu id tài khoản cần xóa

    const userFullNameElement = document.getElementById('userFullNameToDelete');
    userFullNameElement.textContent = fullName; // Điền tên tài khoản vào modal

    $('#delete-user-account').modal('show'); // Hiển thị modal
}
function deleteUserAccount() {
    if (userIdToDelete) {
        fetch(`/admin/user-account-ctrl/${userIdToDelete}`, { method: 'DELETE' })
            .then(response => {
                if (!response.ok) throw new Error('Không thể xóa tài khoản');
                $('#delete-user-account').modal('hide'); // Đóng modal
                showAlert('Xóa tài khoản thành công!', 'alert-light-success'); // Thông báo thành công
                loadUserAccounts(currentUserAccountPage); // Tải lại danh sách tài khoản
            })
            .catch(error => {
                console.error('Lỗi:', error);
                showAlert('Xóa tài khoản thất bại!' + error.message, 'alert-light-danger'); // Thông báo thất bại
            });
    }
}

function viewUserAccount(id) {
    fetch(`/admin/user-account-ctrl/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('Không thể lấy thông tin người dùng');
            return response.json();
        })
        .then(data => {
            document.getElementById('userFullName').textContent = data.fullName;
            document.getElementById('userPhone').textContent = formatPhoneNumber(data.phone) || '';
            document.getElementById('userEmail').textContent = data.email || '';
            document.getElementById('userAddress').textContent = data.address || '';

            const petsContainer = document.getElementById('userPets');
            petsContainer.innerHTML = ''; // Xóa nội dung cũ
            data.pets.forEach(pet => {
                const petImage = pet.image ? `data:image/jpeg;base64,${pet.image}` : '/admin/assets/images/logoPetCare.svg';

                petsContainer.innerHTML += `
                    <div class="pet">
                        <img src="${petImage}" class="card-img-top img-fluid" alt="${pet.name}">
                        <div class="card-body">
                            <h5 class="card-title">${pet.name}</h5>
                            <p>Tuổi: ${pet.age}</p>
                            <p>Giống: ${pet.type}</p>
                            <p>Cân nặng: ${pet.weight} kg</p>
                        </div>
                    </div>
                `;
            });

            // Hiển thị modal
            $('#view-user-account').modal('show');
        })
        .catch(error => {
            console.error('Lỗi:', error);
            alert('Có lỗi xảy ra khi lấy thông tin người dùng.');
        });
}

function prepareEditUser(id, fullName, phone, email, address) {
    document.getElementById('editFullName').value = fullName !== 'null' ? fullName : '';
    document.getElementById('editPhone').value = phone !== 'null' ? phone : ''; // Nếu số điện thoại là null, để trống
    document.getElementById('editEmail').value = email !== 'null' ? email : ''; // Nếu email là null, để trống
    document.getElementById('editAddress').value = address !== 'null' ? address : ''; // Nếu địa chỉ là null, để trống
    window.currentUserId = id;
}

function updateUserAccount() {
    const id = window.currentUserId; // Lấy id người dùng hiện tại
    const fullName = document.getElementById('editFullName').value.trim();
    const phone = document.getElementById('editPhone').value.trim();
    const email = document.getElementById('editEmail').value.trim();
    const address = document.getElementById('editAddress').value.trim();

    if (!fullName) {
        showAlert('Tên người dùng không được để trống!', 'alert-light-danger');
        return;
    }

    fetch(`/admin/user-account-ctrl/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, phone, email, address }),
    })
        .then(response => {
            if (!response.ok) throw new Error('Failed to update user account');
            return response.json();
        })
        .then(() => {
            $('#edit-user-account').modal('hide');
            showAlert('Cập nhật người dùng thành công!', 'alert-light-success');
            loadUserAccounts(); // Tải lại danh sách người dùng
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Cập nhật người dùng thất bại!' + error.message, 'alert-light-danger');
        });
}

function createUserAccount() {
    const nameInput = document.getElementById('newUserName');
    const phoneInput = document.getElementById('newUserPhone');
    const emailInput = document.getElementById('newUserEmail');
    const addressInput = document.getElementById('newUserAddress');

    if (!nameInput || !phoneInput || !emailInput || !addressInput) {
        console.error("Một số trường nhập liệu không tồn tại.");
        showAlert('Một số trường nhập liệu không tồn tại!', 'alert-light-danger');
        return;
    }

    const name = nameInput.value.trim();
    const phone = phoneInput.value.trim();
    const email = emailInput.value.trim();
    const address = addressInput.value.trim();

    if (!name || !phone || !email || !address) {
        showAlert('Tất cả các trường đều phải được điền!', 'alert-light-danger');
        return;
    }

    const userData = {
        fullName: name,
        phone: phone,
        email: email,
        address: address,
    };

    console.log("Dữ liệu người dùng:", userData);

    fetch('/admin/user-account-ctrl', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || 'Failed to create user account');
                });
            }
            return response.json();
        })
        .then(() => {
            $('#create-user-account').modal('hide');
            document.getElementById('createUserForm').reset(); // Reset form
            showAlert('Thêm mới tài khoản người dùng thành công!', 'alert-light-success');
            loadUserAccounts();
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('Thêm mới tài khoản người dùng thất bại: ' + error.message, 'alert-light-danger');
        });
}

function formatPhoneNumber(phone) {
    if (!phone) return ''; // Trả về chuỗi rỗng nếu phone là null hoặc undefined
    return phone.replace(/(\d{3})(\d{3})(\d+)/, '$1 $2 $3');
}
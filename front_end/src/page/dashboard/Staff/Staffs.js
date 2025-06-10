import { getListStaff } from "@/src/redux/slices/userSlice";
import React, { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  Button,
  Form,
  Input,
  message,
  Modal,
  Select,
  Space,
  Table,
  Tag,
} from "antd";
import { getListRoomsByRole } from "@/src/redux/slices/roomSlice";
import roomApi from "@/src/api/roomApi";
import authApi from "@/src/api/authApi";
import { SearchOutlined } from "@ant-design/icons";

const { Option } = Select;

export default function Staffs() {
  const dispatch = useDispatch();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const { listStaff, loading } = useSelector((state) => state.user);
  const { listBuilding } = useSelector((state) => state.building);
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [buildingId, setBuildingId] = useState(null);
  const [form] = Form.useForm();
  const [searchText, setSearchText] = useState("");
  const [searchedColumn, setSearchedColumn] = useState("");
  const searchInput = useRef(null);

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchedColumn(dataIndex);
  };

  const handleReset = (clearFilters) => {
    clearFilters();
    setSearchText("");
  };

  const getColumnSearchProps = (dataIndex) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => (
      <div style={{ padding: 8 }}>
        <Input
          ref={searchInput}
          placeholder={`Tìm kiếm ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={(e) => {
            setSelectedKeys(e.target.value ? [e.target.value] : []);
            confirm({ closeDropdown: false }); // confirm ngay khi gõ, không đóng dropdown
          }}
          onPressEnter={() => confirm()}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            onClick={() => clearFilters()}
            size="small"
            style={{ width: 90 }}
          >
            Xóa
          </Button>
        </Space>
      </div>
    ),

    filterIcon: (filtered) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
    onFilter: (value, record) =>
      record[dataIndex]
        ? record[dataIndex]
            .toString()
            .toLowerCase()
            .includes(value.toLowerCase())
        : "",
    onFilterDropdownVisibleChange: (visible) => {
      if (visible) {
        setTimeout(() => searchInput.current?.select(), 100);
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <span>{text}</span>
      ) : (
        text || <span className="text-gray-400 italic">Chưa có</span>
      ),
  });

  const handleAssignRooms = (staff) => {
    setSelectedStaff(staff);
    setIsModalOpen(true);
  };
  const handleCreated = async () => {
    const res = await roomApi.setRoomManager(
      form.getFieldValue("room_id"),
      selectedStaff.email
    );
    if (res) {
      setIsModalOpen(false);
      form.resetFields();
      dispatch(getListRoomsByRole());
      message.success("Gán quản lý thành công");
    }
  };
  useEffect(() => {
    if (buildingId) {
      const data = { buildingId: buildingId };
      dispatch(getListRoomsByRole(data));
    }
  }, [buildingId]);

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
      width: 70,
      sorter: (a, b) => a.id - b.id,
    },
    {
      title: "Họ và tên",
      dataIndex: "fullName",
      key: "fullName",
      sorter: (a, b) => a.fullName.localeCompare(b.fullName),
      ...getColumnSearchProps("fullName"),
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
      sorter: (a, b) => (a.email || "").localeCompare(b.email || ""),
      ...getColumnSearchProps("email"),
    },
    {
      title: "Ngày sinh",
      dataIndex: "dateOfBirth",
      key: "dateOfBirth",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
      sorter: (a, b) =>
        new Date(a.dateOfBirth || 0).getTime() -
        new Date(b.dateOfBirth || 0).getTime(),
    },
    {
      title: "CMND/CCCD",
      dataIndex: "identityNumber",
      key: "identityNumber",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
      sorter: (a, b) =>
        (a.identityNumber || "").localeCompare(b.identityNumber || ""),
      ...getColumnSearchProps("identityNumber"),
    },
    {
      title: "Địa chỉ",
      dataIndex: "address",
      key: "address",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
      sorter: (a, b) => (a.address || "").localeCompare(b.address || ""),
    },
    {
      title: "Số điện thoại",
      dataIndex: "phoneNumber",
      key: "phoneNumber",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
      sorter: (a, b) =>
        (a.phoneNumber || "").localeCompare(b.phoneNumber || ""),
      ...getColumnSearchProps("phoneNumber"),
    },

    {
      title: "Quản lý phòng",
      key: "manageRooms",
      render: (_, record) => (
        <Button onClick={() => handleAssignRooms(record)}>Gán phòng</Button>
      ),
    },
  ];
  const handleCreateStaff = async (values) => {
    try {
      const response = await authApi.createAccount({
        ...values,
        role: "STAFF",
      });
      if (response) {
        message.success("Tạo nhân viên thành công");
        form.resetFields();
        setIsModalVisible(false);
        dispatch(getListStaff());
      }
      // TODO: reload danh sách nếu cần
    } catch (error) {}
  };
  return (
    <div className="p-6 bg-white shadow-md rounded-xl">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-semibold">Danh sách nhân viên</h1>
        <Button type="primary" onClick={() => setIsModalVisible(true)}>
          Tạo nhân viên
        </Button>
      </div>
      <Table
        dataSource={listStaff?.content || []}
        columns={columns}
        rowKey="id"
        pagination={{ pageSize: 5 }}
        bordered
        loading={loading}
      />
      <Modal
        title="Gán phòng"
        open={isModalOpen}
        onCancel={() => {
          setIsModalOpen(false);
          form.resetFields();
        }}
        onOk={handleCreated}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="buildingId"
            label="Tòa nhà"
            rules={[{ required: true }]}
          >
            <Select
              placeholder="Chọn tòa nhà"
              onChange={(value) => {
                setBuildingId(value);

                form.setFieldsValue({ room_number: null });
              }}
            >
              {listBuilding &&
                listBuilding.map((b) => (
                  <Option key={b.id} value={b.id}>
                    {b.name}
                  </Option>
                ))}
            </Select>
          </Form.Item>
          <Form.Item name="room_id" label="Phòng" rules={[{ required: true }]}>
            <Select
              placeholder="Chọn phòng"
              onClick={() => {
                if (!buildingId) {
                  message.warning("Vui lòng chọn tòa nhà trước khi chọn phòng");
                }
              }}
              disabled={!buildingId}
            >
              {listRoomsByRole &&
                listRoomsByRole.content
                  .filter((val) => val.isActive && val.staff == null)
                  .map((r) => (
                    <Option key={r.roomNumber} value={r.roomId}>
                      {r.roomNumber}
                    </Option>
                  ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="Tạo nhân viên"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        onOk={() => form.submit()}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical" onFinish={handleCreateStaff}>
          <Form.Item
            label="Họ và tên"
            name="fullName"
            rules={[{ required: true, message: "Vui lòng nhập họ và tên" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: "Vui lòng nhập email" },
              { type: "email", message: "Email không hợp lệ" },
            ]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

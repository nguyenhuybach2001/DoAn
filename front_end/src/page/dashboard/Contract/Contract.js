import React, { useEffect, useState } from "react";
import {
  Button,
  Card,
  Table,
  Typography,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  DatePicker,
  Upload,
  message,
  InputNumber,
} from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { getListRoomsByRole } from "@/src/redux/slices/roomSlice";
import { getListBuilding } from "@/src/redux/slices/buildingSlice";
// import { getListCustomers } from "@/src/redux/slices/customerSlice";
import contractApi from "@/src/api/contractApi";
import { getListContracts } from "@/src/redux/slices/contractSlice";

const { Title } = Typography;
const { Option } = Select;

export default function Contract() {
  const [showModal, setShowModal] = useState(false);
  const [form] = Form.useForm();
  const [buildingId, setBuildingId] = useState(null);
  const dispatch = useDispatch();

  const { listBuilding } = useSelector((state) => state.building);
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const { listCustomer } = useSelector((state) => state.user);
  const { listContracts, loading } = useSelector((state) => state.contract);
  useEffect(() => {
    if (buildingId) {
      const data = { buildingId: buildingId };
      dispatch(getListRoomsByRole(data));
    }
  }, [buildingId]);
  const selectedRoomNumber = Form.useWatch("room_number", form);

  useEffect(() => {
    if (selectedRoomNumber && listRoomsByRole) {
      const room = listRoomsByRole.content.find(
        (r) => r.roomNumber === selectedRoomNumber
      );
      if (room) {
        form.setFieldsValue({ rentPrice: room.price });
      }
    }
  }, [selectedRoomNumber, listRoomsByRole]);
  const handleCreated = async () => {
    try {
      const values = await form.validateFields();
      const [startDate, endDate] = values.dateRange;
      console.log(values, startDate, endDate);
      const formData = new FormData();
      formData.append("startDate", startDate.format("YYYY-MM-DD"));
      formData.append("endDate", endDate.format("YYYY-MM-DD"));
      formData.append("rentPrice", values.rentPrice);
      formData.append("status", values.status);
      formData.append("customerId", values.customerId);
      formData.append("room_number", values.room_number);
      formData.append("buildingId", values.buildingId);
      if (values.pdfFile?.file) {
        formData.append("pdfFile", values.pdfFile.file);
      }

      await contractApi.createContract(formData);
      message.success("Tạo hợp đồng thành công");
      setBuildingId(null);
      dispatch(getListContracts());
      dispatch(getListRoomsByRole());
      setShowModal(false);
      form.resetFields();
    } catch (err) {
      message.error("Tạo hợp đồng thất bại");
    }
  };

  const columns = [
    { title: "Mã hợp đồng", dataIndex: "id", key: "id" },
    { title: "Phòng", dataIndex: "room_number", key: "roomNumber" },
    { title: "Tòa nhà", dataIndex: "buildingName", key: "buildingName" },
    { title: "Khách thuê", dataIndex: "tenantName", key: "tenantName" },
    { title: "Ngày bắt đầu", dataIndex: "startDate", key: "startDate" },
    { title: "Ngày kết thúc", dataIndex: "endDate", key: "endDate" },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (status) => {
        const color =
          status === "IN_PROGRESS"
            ? "green"
            : status === "EXPIRED"
            ? "red"
            : "orange";
        return <Tag color={color}>{status}</Tag>;
      },
    },
  ];
  return (
    <div style={{ padding: "24px" }}>
      <Card>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: 16,
          }}
        >
          <Title level={4}>Quản lý hợp đồng</Title>
          <Button type="primary" onClick={() => setShowModal(true)}>
            Tạo hợp đồng
          </Button>
        </div>

        <Table
          dataSource={listContracts && listContracts}
          columns={columns}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="Tạo hợp đồng"
        open={showModal}
        onCancel={() => {
          setShowModal(false);
          form.resetFields();
        }}
        onOk={handleCreated}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="customerId"
            label="Khách hàng"
            rules={[{ required: true }]}
          >
            <Select placeholder="Chọn khách hàng">
              {listCustomer &&
                listCustomer.content.map((cus) => (
                  <Option key={cus.id} value={cus.id}>
                    {cus.fullName}
                  </Option>
                ))}
            </Select>
          </Form.Item>

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
          <Form.Item
            name="room_number"
            label="Phòng"
            rules={[{ required: true }]}
          >
            <Select
              placeholder="Chọn phòng"
              onClick={() => {
                if (!buildingId) {
                  message.warning("Vui lòng chọn tòa nhà trước khi chọn phòng");
                }
              }}
              disabled={!buildingId} // cũng disable luôn nếu chưa chọn building
            >
              {listRoomsByRole &&
                listRoomsByRole.content
                  .filter((val) => val.isActive && val.status == "AVAILABLE")
                  .map((r) => (
                    <Option key={r.roomNumber} value={r.roomNumber}>
                      {r.roomNumber}
                    </Option>
                  ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="dateRange"
            label="Thời gian thuê"
            rules={[
              {
                required: true,
                message: "Vui lòng chọn khoảng thời gian thuê",
              },
            ]}
          >
            <DatePicker.RangePicker style={{ width: "100%" }} />
          </Form.Item>

          <Form.Item
            name="rentPrice"
            label="Giá thuê (VNĐ)"
            rules={[{ required: true }]}
          >
            <Input className="text-black " disabled />
          </Form.Item>

          <Form.Item
            name="status"
            label="Trạng thái"
            initialValue="IN_PROGRESS"
            rules={[{ required: true }]}
          >
            <Select>
              <Option value="IN_PROGRESS">Đang hiệu lực</Option>
              <Option value="EXPIRED">Hết hạn</Option>
            </Select>
          </Form.Item>

          <Form.Item name="pdfFile" label="Tệp hợp đồng (.pdf)">
            <Upload beforeUpload={() => false} maxCount={1}>
              <Button icon={<UploadOutlined />}>Chọn file</Button>
            </Upload>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

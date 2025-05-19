import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Form, Input, message, Modal, Table, Tag } from "antd";
import authApi from "@/src/api/authApi";
import { getListCustomer } from "@/src/redux/slices/userSlice";

export default function Customer() {
  const { listCustomer, loading } = useSelector((state) => state.user);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const dispatch = useDispatch();
  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
      width: 70,
    },
    {
      title: "Họ và tên",
      dataIndex: "fullName",
      key: "fullName",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
    },
    {
      title: "Ngày sinh",
      dataIndex: "dateOfBirth",
      key: "dateOfBirth",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
    },
    {
      title: "CMND/CCCD",
      dataIndex: "identityNumber",
      key: "identityNumber",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
    },
    {
      title: "Địa chỉ",
      dataIndex: "address",
      key: "address",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
    },
    {
      title: "Số điện thoại",
      dataIndex: "phoneNumber",
      key: "phoneNumber",
      render: (text) =>
        text || <span className="text-gray-400 italic">Chưa có</span>,
    },
  ];
  const handleCreateCustomer = async (values) => {
    console.log(values);
    try {
      const response = await authApi.createAccount({
        ...values,
        role: "CUSTOMER",
      });
      if (response) {
        message.success("Tạo khách hàng thành công");
        console.log(response);
        form.resetFields();
        setIsModalVisible(false);
        dispatch(getListCustomer());
      }
      // TODO: reload danh sách nếu cần
    } catch (error) {}
  };
  return (
    <div className="p-6 bg-white shadow-md rounded-xl">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-semibold">Danh sách khách hàng</h1>
        <Button type="primary" onClick={() => setIsModalVisible(true)}>
          Tạo khách hàng
        </Button>
      </div>
      <Table
        dataSource={listCustomer?.content || []}
        columns={columns}
        rowKey="id"
        pagination={{ pageSize: 5 }}
        bordered
        loading={loading}
      />

      <Modal
        title="Tạo khách hàng"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        onOk={() => form.submit()}
        okText="Tạo"
        cancelText="Hủy"
      >
        <Form form={form} layout="vertical" onFinish={handleCreateCustomer}>
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

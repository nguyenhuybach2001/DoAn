"use client";
import { getListRoomsByRole } from "@/src/redux/slices/roomSlice";
import { HomeOutlined } from "@ant-design/icons";
import { Button, Card, Form, Input, Modal, Tag } from "antd";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

export default function MyRental() {
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const [openModal, setOpenModal] = useState(false);
  const dispatch = useDispatch();
  useEffect(() => {
    dispatch(getListRoomsByRole());
  }, []);
  const rentalData = [
    {
      room: "Room A203",
      building: "Sunshine 1",
      area: 25,
      price: 3000000,
      status: "active",
    },
    {
      room: "Room B102",
      building: "Green Home",
      area: 20,
      price: 3500000,
      status: "expiring",
    },
    {
      room: "Room C301",
      building: "Ocean View",
      area: 30,
      price: 4200000,
      status: "pending",
    },
    {
      room: "Room D210",
      building: "City Center Plaza",
      area: 22,
      price: 3800000,
      status: "renewal",
    },
  ];

  const getStatusTag = (status) => {
    switch (status) {
      case "active":
        return <Tag color="green">Đang thuê</Tag>;
      case "expiring":
        return <Tag color="gold">Sắp hết hạn</Tag>;
      case "pending":
        return <Tag color="default">Đang chờ duyệt</Tag>;
      case "renewal":
        return <Tag color="blue">Chờ gia hạn</Tag>;
      default:
        return null;
    }
  };
  console.log(listRoomsByRole, "listRoomsByRole");
  const [form] = Form.useForm();
  return (
    <div className="px-6 py-8 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-semibold mb-6">My Rental</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {rentalData.map((rental, idx) => (
          <Card
            key={idx}
            title={
              <div className="flex items-center gap-2">
                <HomeOutlined />
                <span>{rental.room}</span>
              </div>
            }
            bordered={false}
            className="shadow-md"
          >
            <p className="text-sm text-gray-600">{rental.building}</p>
            <p className="mt-1 text-sm">Diện tích: {rental.area} m²</p>
            <p className="text-sm">
              Giá thuê: {rental.price.toLocaleString()} VND/tháng
            </p>
            <div className="mt-2">{getStatusTag(rental.status)}</div>

            <div className="flex flex-col gap-2 mt-4">
              <Button type="primary">Xem chi tiết</Button>
              <Button>Quản lý hợp đồng</Button>
              {/* <Button>Thanh toán</Button> */}
              <Button
                danger
                onClick={() => {
                  setOpenModal(true);
                }}
              >
                Gửi yêu cầu bảo trì
              </Button>
            </div>
          </Card>
        ))}
      </div>
      <Modal
        open={openModal}
        footer={false}
        onCancel={() => setOpenModal(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item>
            <Input placeholder="Nhập yêu cầu bảo trì" />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              onClick={() => {
                form.resetFields();
                setOpenModal(false);
              }}
            >
              Gửi yêu cầu
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

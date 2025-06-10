import React, { useState, useEffect, useMemo } from "react";
import {
  Table,
  Button,
  Modal,
  InputNumber,
  Select,
  Form,
  message,
  Card,
  Tag,
} from "antd";
import { Bar } from "react-chartjs-2";
import { PlusOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import roomUtilityApi from "@/src/api/roomUtilityApi";
import { getUtilityUsagePerMonth } from "@/src/redux/slices/statisticSlice";
import dayjs from "dayjs";

const electricityPrice = 3000;
const waterPrice = 14000;

export default function Bills() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedRooms, setSelectedRooms] = useState([]);
  const { utilityUsagePerMonth, loading } = useSelector(
    (state) => state.statistic
  );
  const dispatch = useDispatch();
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const [form] = Form.useForm();
  const [year, setYear] = useState(new Date().getFullYear());
  useEffect(() => {
    dispatch(getUtilityUsagePerMonth({ year }));
  }, [year]);
  const allRooms =
    listRoomsByRole?.content.map((room) => ({
      value: room.roomId,
      label: room.roomNumber,
    })) || [];

  const watchedValues = Form.useWatch([], form);

  const previewBills = useMemo(() => {
    return selectedRooms.map((roomId) => {
      const electricity = watchedValues?.[`electricity-${roomId}`] || 0;
      const water = watchedValues?.[`water-${roomId}`] || 0;
      const total = electricity * electricityPrice + water * waterPrice;
      return {
        roomId,
        electricity,
        water,
        total,
      };
    });
  }, [selectedRooms, watchedValues]);

  const chartData = useMemo(() => {
    const labels = Object.keys(utilityUsagePerMonth).sort();
    const electricityData = labels.map(
      (m) => utilityUsagePerMonth[m]?.electricity * electricityPrice || 0
    );
    const waterData = labels.map(
      (m) => utilityUsagePerMonth[m]?.water * waterPrice || 0
    );

    return {
      labels: labels.map((m) => `Tháng ${m}`),
      datasets: [
        {
          label: "Tiền điện (VND)",
          data: electricityData,
          backgroundColor: "#3b82f6",
          borderRadius: 10,
        },
        {
          label: "Tiền nước (VND)",
          data: waterData,
          backgroundColor: "#10b981",
          borderRadius: 10,
        },
      ],
    };
  }, [utilityUsagePerMonth]);
  const columns = [
    {
      title: "Phòng",
      dataIndex: "roomId",
      render: (text) => (
        <Tag color="blue">
          {
            listRoomsByRole.content.find((room) => room.roomId === text)
              .roomNumber
          }
        </Tag>
      ),
    },
    {
      title: "Số điện",
      dataIndex: "electricity",
      render: (val) => `${val} số`,
    },
    {
      title: "Số nước",
      dataIndex: "water",
      render: (val) => `${val} số`,
    },
    {
      title: "Tổng tiền",
      dataIndex: "total",
      render: (val) => (
        <span className="font-semibold text-green-600">
          {val.toLocaleString()} VND
        </span>
      ),
    },
  ];
  const handleCreateAllBills = async () => {
    form.validateFields().then(async (values) => {
      const allBills = selectedRooms.map((room) => {
        const usage_electricity = values[`electricity-${room}`] || 0;
        const usage_water = values[`water-${room}`] || 0;
        return {
          building_id: 1,
          room_number: listRoomsByRole.content.find(
            (val) => val.roomId === room
          ).roomNumber,
          usage_electricity,
          usage_water,
          date: `${dayjs().format("YYYY-MM-DD")}`,
          amount: usage_electricity * 3000 + usage_water * 14000,
        };
      });

      const res = await roomUtilityApi.create(allBills);
      if (res) {
        message.success("Tạo hóa đơn thành công");
        setIsModalOpen(false);
        setSelectedRooms([]);
        form.resetFields();
        dispatch(getUtilityUsagePerMonth({ year }));
      }
    });
  };

  return (
    <div className="p-6 space-y-8">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold text-gray-800">Quản lý hóa đơn</h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setIsModalOpen(true)}
        >
          Tạo hóa đơn
        </Button>
      </div>

      <div className="flex justify-end mb-4">
        <Select
          value={year}
          onChange={setYear}
          options={Array.from({ length: 5 }, (_, i) => ({
            label: `${new Date().getFullYear() - i}`,
            value: new Date().getFullYear() - i,
          }))}
          style={{ width: 120 }}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card
          title="Thống kê tiền điện & nước"
          className="rounded-2xl shadow-md"
        >
          <Bar data={chartData} options={{ responsive: true }} />
        </Card>
        <Card title="Bảng thống kê" className="rounded-2xl shadow-md">
          <Table
            dataSource={Object.entries(utilityUsagePerMonth)
              .sort(([a], [b]) => parseInt(a) - parseInt(b))
              .map(([month, usage]) => ({
                key: month,
                month: `Tháng ${parseInt(month)}`,
                electricity: usage.electricity,
                water: usage.water,
                total:
                  usage.electricity * electricityPrice +
                  usage.water * waterPrice,
              }))}
            scroll={{ y: 250 }}
            columns={[
              { title: "Tháng", dataIndex: "month" },
              { title: "Số điện", dataIndex: "electricity" },
              { title: "Số nước", dataIndex: "water" },
              {
                title: "Tổng tiền",
                dataIndex: "total",
                render: (val) => (
                  <span className="text-green-600 font-medium">
                    {val.toLocaleString()} VND
                  </span>
                ),
              },
            ]}
            pagination={false}
          />
        </Card>
      </div>

      <Modal
        style={selectedRooms.length > 0 ? { top: 20 } : {}}
        title="Tạo hóa đơn cho nhiều phòng"
        open={isModalOpen}
        onCancel={() => {
          setIsModalOpen(false);
          setSelectedRooms([]);
          form.resetFields(); // reset form
        }}
        onOk={() => {
          selectedRooms.length > 0
            ? handleCreateAllBills()
            : message.error("Phòng chưa được chọn");
        }}
        okText="Tạo hóa đơn"
        width={700}
      >
        <div className="mb-4">
          <label className="font-semibold">Chọn phòng</label>
          <Select
            mode="multiple"
            placeholder="Chọn các phòng bạn quản lý"
            options={allRooms}
            className="w-full"
            value={selectedRooms}
            onChange={setSelectedRooms}
          />
        </div>
        <Form
          form={form}
          layout="vertical"
          className="max-h-40 overflow-y-auto pr-2"
        >
          {selectedRooms.map((room) => (
            <div
              key={room}
              className="border rounded-lg p-4 mb-4 shadow-sm bg-white"
            >
              <h3 className="text-lg font-semibold mb-3">{`Phòng ${
                listRoomsByRole.content.find((val) => val.roomId === room)
                  .roomNumber
              }`}</h3>
              <div className="grid grid-cols-2 gap-4">
                <Form.Item
                  label="Số điện"
                  name={`electricity-${room}`}
                  rules={[{ required: true, message: "Vui lòng nhập số điện" }]}
                >
                  <InputNumber
                    min={0}
                    className="w-full"
                    placeholder="Nhập số điện"
                  />
                </Form.Item>
                <Form.Item
                  label="Số nước"
                  name={`water-${room}`}
                  rules={[{ required: true, message: "Vui lòng nhập số nước" }]}
                >
                  <InputNumber
                    min={0}
                    className="w-full"
                    placeholder="Nhập số nước"
                  />
                </Form.Item>
              </div>
            </div>
          ))}
        </Form>

        {selectedRooms.length > 0 && (
          <div className="mt-4">
            <h4 className="font-bold text-gray-700">Hóa đơn sẽ tạo:</h4>
            <Table
              scroll={{ y: 70 }}
              dataSource={previewBills}
              columns={columns}
              size="small"
              pagination={false}
              rowKey={(r) => r.roomId}
            />
          </div>
        )}
      </Modal>
    </div>
  );
}

import Image from "next/image";
import React from "react";
import img from "@/asset/images/image2.png";
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  Title,
  CategoryScale,
  LinearScale,
  BarElement,
} from "chart.js";
import { Bar, Doughnut } from "react-chartjs-2";
import {
  AuditOutlined,
  TeamOutlined,
  UserOutlined,
  HomeOutlined,
} from "@ant-design/icons";
import { useSelector } from "react-redux";
import { Card, Col, Row } from "antd";

ChartJS.register(
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend,
  Title
);

export default function DashBoard() {
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const { listContracts } = useSelector((state) => state.contract);
  const { listCustomer, listStaff } = useSelector((state) => state.user);

  const totalRooms = listRoomsByRole?.content?.length || 0;
  const rented =
    listRoomsByRole?.content?.filter((r) => r.status === "RENTED").length || 0;
  const available =
    listRoomsByRole?.content?.filter((r) => r.status === "AVAILABLE").length ||
    0;
  const doughnutData = {
    labels: ["Available", "Rented"],
    datasets: [
      {
        data: [available, rented],
        backgroundColor: ["#F87171", "#60A5FA"],
        hoverOffset: 4,
      },
    ],
  };

  const doughnutOptions = {
    plugins: {
      legend: { position: "bottom" },
      title: { display: false },
    },
  };

  const barLabels = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
  ];
  const barData = {
    labels: barLabels,
    datasets: [
      {
        label: "Profits",
        data: [12, 19, 3, 5, 2, 3, 9],
        backgroundColor: "#60A5FA",
      },
      {
        label: "Expenses",
        data: [2, 3, 20, 5, 1, 4, 2],
        backgroundColor: "#F87171",
      },
    ],
  };

  const barOptions = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true, text: "Monthly Report" },
    },
  };

  return (
    <div className="px-8 py-6 space-y-8">
      {/* Greeting + Image */}
      <Row gutter={32} align="middle">
        <Col span={16}>
          <h1 className="text-3xl font-bold">Welcome back 👋</h1>
          <p className="text-lg text-gray-600 mt-1">
            Here is your admin dashboard
          </p>
        </Col>
        <Col span={8}>
          <Image
            src={img}
            alt="Dashboard"
            className="rounded-2xl w-full object-cover"
          />
        </Col>
      </Row>

      {/* Statistic Cards */}
      <Row gutter={24}>
        <Col span={6}>
          <Card className="bg-blue-100 rounded-xl">
            <div className="flex items-center gap-4">
              <HomeOutlined className="text-2xl text-blue-500" />
              <div>
                <p className="text-lg">Total Rooms</p>
                <p className="text-3xl font-bold">{totalRooms}</p>
              </div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card className="bg-green-100 rounded-xl">
            <div className="flex items-center gap-4">
              <AuditOutlined className="text-2xl text-green-600" />
              <div>
                <p className="text-lg">Contracts</p>
                <p className="text-3xl font-bold">
                  {listContracts?.length || 0}
                </p>
              </div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card className="bg-yellow-100 rounded-xl">
            <div className="flex items-center gap-4">
              <UserOutlined className="text-2xl text-yellow-600" />
              <div>
                <p className="text-lg">Customers</p>
                <p className="text-3xl font-bold">
                  {listCustomer?.content?.length || 0}
                </p>
              </div>
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card className="bg-purple-100 rounded-xl">
            <div className="flex items-center gap-4">
              <TeamOutlined className="text-2xl text-purple-600" />
              <div>
                <p className="text-lg">Staff</p>
                <p className="text-3xl font-bold">
                  {listStaff?.content?.length || 0}
                </p>
              </div>
            </div>
          </Card>
        </Col>
      </Row>

      {/* Doughnut + Breakdown */}
      <Row gutter={24}>
        <Col span={12}>
          <Card title="Room Status">
            <div className="flex justify-center">
              <Doughnut data={doughnutData} options={doughnutOptions} />
            </div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="Breakdown">
            <div className="space-y-4">
              <div className="flex items-center gap-3">
                <span className="w-3 h-3 bg-[#F87171] rounded-full" />
                <p className="text-md">Available: {available}</p>
              </div>
              <div className="flex items-center gap-3">
                <span className="w-3 h-3 bg-[#60A5FA] rounded-full" />
                <p className="text-md">Rented: {rented}</p>
              </div>
            </div>
          </Card>
        </Col>
      </Row>

      {/* Bar Chart */}
      <Row>
        <Col span={24}>
          <Card title="Monthly Financial Overview">
            <Bar data={barData} options={barOptions} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}

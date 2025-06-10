import Image from "next/image";
import React, { useEffect, useState } from "react";
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
  PointElement,
  LineElement,
  Filler,
} from "chart.js";
import { Bar, Doughnut, Line } from "react-chartjs-2";
import {
  AuditOutlined,
  TeamOutlined,
  UserOutlined,
  HomeOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { Card, Col, DatePicker, Row } from "antd";
import {
  getContractQuantity,
  getContractsPerMonth,
  getProfit,
  getProfitPerMonth,
  getTotalExpenses,
  getTotalExpensesPerMonth,
  getTotalIncome,
  getTotalIncomePerMonth,
  getUtilityUsage,
  getUtilityUsagePerMonth,
  getUtilityUsagePerMonthByElectronic,
  getUtilityUsagePerMonthByWater,
} from "@/src/redux/slices/statisticSlice";
import dayjs from "dayjs";

ChartJS.register(
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  Tooltip,
  Legend,
  Title,
  Filler
);

export default function DashBoard({ setTab }) {
  const currentYear = dayjs().year();
  const [year, setYear] = useState(currentYear);
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const { listContracts } = useSelector((state) => state.contract);
  const { listCustomer, listStaff } = useSelector((state) => state.user);
  const {
    totalIncome,
    incomePerMonth,
    totalExpenses,
    expensesPerMonth,
    profit,
    profitPerMonth,
    contractQuantity,
    contractsPerMonth,
    utilityUsage,
    utilityUsagePerMonth,
    loading,
  } = useSelector((state) => state.statistic);
  const dispatch = useDispatch();
  useEffect(() => {
    const data = { year: Number(year) };
    dispatch(getTotalIncome());
    dispatch(getTotalIncomePerMonth(data));
    dispatch(getTotalExpenses());
    dispatch(getTotalExpensesPerMonth(data));
    dispatch(getProfit());
    dispatch(getProfitPerMonth(data));
    // dispatch(getContractQuantity());
    // dispatch(getContractsPerMonth());
    dispatch(getUtilityUsage());
    dispatch(getUtilityUsagePerMonth({ year }));
  }, []);
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
    "August",
    "September",
    "October",
    "November",
    "December",
  ];
  const monthOrder = [
    "01",
    "02",
    "03",
    "04",
    "05",
    "06",
    "07",
    "08",
    "09",
    "10",
    "11",
    "12",
  ];
  const barData = {
    labels: barLabels,
    datasets: [
      {
        label: "Lợi nhuận",
        data:
          profitPerMonth &&
          monthOrder.map((month) => profitPerMonth[month] || 0),
        backgroundColor: "#34D399",
      },
      {
        label: "Tổng thu",
        data:
          incomePerMonth &&
          monthOrder.map((month) => incomePerMonth[month] || 0),
        backgroundColor: "#60A5FA",
      },
      {
        label: "Tổng chi",
        data:
          expensesPerMonth &&
          monthOrder.map((month) => expensesPerMonth[month] || 0),
        backgroundColor: "#F87171",
      },
    ],
  };
  const handleYearChange = (date, dateString) => {
    setYear(dateString);
    const data = { year: Number(dateString) };
    dispatch(getProfitPerMonth(data));
    dispatch(getTotalIncomePerMonth(data));
    dispatch(getTotalExpensesPerMonth(data));
  };

  const utilityDataSorted = Object.entries(utilityUsagePerMonth || {})
    .sort(([a], [b]) => parseInt(a) - parseInt(b))
    .map(([month, usage]) => ({
      month: `Tháng ${parseInt(month)}`,
      electricity: usage.electricity,
      water: usage.water,
    }));
  const lineData = {
    labels: barLabels,
    datasets: [
      {
        label: "Điện (kWh)",
        data: utilityDataSorted.map((item) => item.electricity),
        fill: false,
        borderColor: "#3B82F6",
        backgroundColor: "#3B82F6",
        tension: 0.4,
      },
      {
        label: "Nước (m³)",
        data: utilityDataSorted.map((item) => item.water),
        fill: false,
        borderColor: "#10B981",
        backgroundColor: "#10B981",
        tension: 0.4,
      },
    ],
  };

  const lineOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "top",
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            return `${
              context.dataset.label
            }: ${context.raw.toLocaleString()}`;
          },
        },
      },
    },
    scales: {
      y: {
        min: 0,
        ticks: {
          callback: (value) => `${value} `,
        },
        title: {
          display: true,
          text: "Số liệu ",
        },
      },
      x: {
        title: {
          display: true,
          text: "Tháng",
        },
      },
    },
  };

  const barOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "top",
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            return `${
              context.dataset.label
            }: ${context.raw.toLocaleString()} VNĐ`;
          },
        },
      },
    },
    scales: {
      y: {
        min: 0,
        ticks: {
          callback: (value) => `${value} `,
        },
        title: {
          display: true,
          text: "Số tiền (VNĐ)",
        },
      },
      x: {
        title: {
          display: true,
          text: "Tháng",
        },
      },
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
          <Card
            className="bg-blue-100 rounded-xl cursor-pointer"
            onClick={() => {
              setTab("3");
            }}
          >
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
          <Card
            className="bg-green-100 rounded-xl cursor-pointer"
            onClick={() => {
              setTab("6");
            }}
          >
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
          <Card
            className="bg-yellow-100 rounded-xl cursor-pointer"
            onClick={() => {
              setTab("2");
            }}
          >
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
          <Card
            className="bg-purple-100 rounded-xl cursor-pointer"
            onClick={() => {
              setTab("5");
            }}
          >
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
          <Card
            title={` Tổng quan tài chính năm ${year}`}
            extra={
              <DatePicker
                onChange={handleYearChange}
                defaultValue={dayjs()}
                picker="year"
              />
            }
          >
            <Bar data={barData} options={barOptions} />
          </Card>
        </Col>
      </Row>
      <Row>
        <Col span={24}>
          <Card
            title={` Tổng quan số điện, nước năm ${year}`}
            extra={
              <DatePicker
                onChange={handleYearChange}
                defaultValue={dayjs()}
                picker="year"
              />
            }
          >
            <Line data={lineData} options={lineOptions} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}

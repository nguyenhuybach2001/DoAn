import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Doughnut, Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  Title,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
} from "chart.js";
import { UserOutlined, HomeOutlined } from "@ant-design/icons";
import dayjs from "dayjs";
import { getUtilityUsagePerMonth } from "@/src/redux/slices/statisticSlice";
import { Select, Spin } from "antd";

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  Title,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement
);

export default function DashBoardStaff() {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  const { listCustomer } = useSelector((state) => state.user);
  const currentYear = dayjs().year();
  const [year, setYear] = useState(currentYear);
  const { utilityUsagePerMonth, loading } = useSelector(
    (state) => state.statistic
  );

  useEffect(() => {
    dispatch(getUtilityUsagePerMonth({ year }));
  }, [year]);

  const rentedRooms =
    listRoomsByRole?.content.filter((room) => room.status === "RENTED") ?? [];
  const availableRooms =
    listRoomsByRole?.content.filter((room) => room.status === "AVAILABLE") ??
    [];

  const chartData = {
    labels: ["Còn trống", "Đã thuê"],
    datasets: [
      {
        data: [availableRooms.length, rentedRooms.length],
        backgroundColor: ["#FBBF24", "#34D399"],
        hoverOffset: 4,
      },
    ],
  };

  const utilityDataSorted = Object.entries(utilityUsagePerMonth || {})
    .sort(([a], [b]) => parseInt(a) - parseInt(b))
    .map(([month, usage]) => ({
      month: `Tháng ${parseInt(month)}`,
      electricity: usage.electricity,
      water: usage.water,
    }));

  const utilityChart = {
    labels: utilityDataSorted.map((item) => item.month),
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

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: "bottom",
      },
    },
    scales: {
      y: {
        beginAtZero: true,
      },
    },
  };

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-4">Staff Dashboard</h1>
      <p className="mb-8 text-gray-600">Overview of your assigned rooms</p>

      <div className="grid grid-cols-4 gap-6 mb-10">
        <div className="p-4 bg-blue-100 rounded-2xl">
          <div className="flex items-center gap-3">
            <HomeOutlined />
            <p className="text-lg font-semibold">Tổng số phòng</p>
          </div>
          <p className="text-4xl font-bold mt-2">
            {listRoomsByRole?.content.length ?? 0}
          </p>
        </div>
        <div className="p-4 bg-green-100 rounded-2xl">
          <div className="flex items-center gap-3">
            <UserOutlined />
            <p className="text-lg font-semibold">Khách thuê</p>
          </div>
          <p className="text-4xl font-bold mt-2">
            {listCustomer?.content.length ?? 0}
          </p>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-8">
        <div className="bg-white rounded-2xl p-6 shadow-md w-fit">
          <h2 className="text-xl font-bold mb-4">Tình trạng phòng</h2>
          <Doughnut data={chartData} width={250} height={250} />
          <div className="mt-4 flex justify-around text-sm font-semibold text-gray-700">
            <p>Còn trống: {availableRooms.length}</p>
            <p>Đã thuê: {rentedRooms.length}</p>
          </div>
        </div>

        <div className="bg-white rounded-2xl p-6 shadow-md w-full">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold">Sử dụng điện & nước ({year})</h2>
            <Select
              value={year}
              onChange={setYear}
              options={Array.from({ length: 5 }, (_, i) => ({
                label: currentYear - i,
                value: currentYear - i,
              }))}
              style={{ width: 100 }}
            />
          </div>
          {loading ? (
            <div className="text-center py-8">
              <Spin />
            </div>
          ) : (
            <Line data={utilityChart} options={chartOptions} />
          )}
        </div>
      </div>
    </div>
  );
}

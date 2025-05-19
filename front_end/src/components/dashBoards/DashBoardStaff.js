import React from "react";
import { useSelector } from "react-redux";
import { Doughnut } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend, Title } from "chart.js";
import {
  UserOutlined,
  CarOutlined,
  AlertOutlined,
  HomeOutlined,
} from "@ant-design/icons";

ChartJS.register(ArcElement, Tooltip, Legend, Title);

export default function DashBoardStaff() {
  const { user } = useSelector((state) => state.auth);
  const { listRooms } = useSelector((state) => state.rooms); // chỉ các phòng được phân công
  const { listCustomer } = useSelector((state) => state.user); // khách thuê trong các phòng đó
  //   const { listVehicles } = useSelector((state) => state.vehicle); // phương tiện liên quan
  //   const { listRequests } = useSelector((state) => state.request); // các yêu cầu từ khách

  const rentedRooms =
    listRooms?.content.filter((room) => room.status === "RENTED") ?? [];
  const availableRooms =
    listRooms?.content.filter((room) => room.status === "AVAILABLE") ?? [];

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

  const chartOptions = {
    responsive: false,
    plugins: {
      legend: {
        position: "bottom",
      },
      title: {
        display: false,
      },
    },
  };
  console.log(listRooms, "jonk");
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
            {listRooms?.content.length ?? 0}
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
        {/* <div className="p-4 bg-yellow-100 rounded-2xl">
          <div className="flex items-center gap-3">
            <CarOutlined />
            <p className="text-lg font-semibold">Phương tiện</p>
          </div>
          <p className="text-4xl font-bold mt-2">
            {listVehicles?.length ?? 0}
          </p>
        </div> */}
        {/* <div className="p-4 bg-red-100 rounded-2xl">
          <div className="flex items-center gap-3">
            <AlertOutlined />
            <p className="text-lg font-semibold">Yêu cầu hỗ trợ</p>
          </div>
          <p className="text-4xl font-bold mt-2">
            {listRequests?.length ?? 0}
          </p>
        </div> */}
      </div>

      <div className="bg-white rounded-2xl p-6 shadow-md w-fit">
        <h2 className="text-xl font-bold mb-4">Tình trạng phòng</h2>
        <Doughnut
          data={chartData}
          options={chartOptions}
          width={250}
          height={250}
        />
        <div className="mt-4 flex justify-around text-sm font-semibold text-gray-700">
          <p>Còn trống: {availableRooms.length}</p>
          <p>Đã thuê: {rentedRooms.length}</p>
        </div>
      </div>
    </div>
  );
}

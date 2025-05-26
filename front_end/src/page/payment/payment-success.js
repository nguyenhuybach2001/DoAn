"use client";
import { CheckCircleOutlined } from "@ant-design/icons";
import { useRouter } from "next/navigation";
import React, { useEffect } from "react";

export default function PaymentSuccess() {
  const router = useRouter();

  // useEffect(() => {
  //   const timer = setTimeout(() => {
  //     router.push("/"); 
  //   }, 3000);

  //   return () => clearTimeout(timer);
  // }, []);

  return (
    <div className="flex items-center justify-center h-screen bg-green-50">
      <div className="bg-white p-8 rounded-2xl shadow-lg text-center">
        <CheckCircleOutlined className="text-green-500 text-6xl mb-4" />
        <h1 className="text-2xl font-semibold text-green-600 mb-2">
          Thanh toán thành công!
        </h1>
        <p className="text-gray-600">
          Bạn sẽ được chuyển về trang chủ trong giây lát...
        </p>
      </div>
    </div>
  );
}

"use client";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import Search from "@/src/components/search/Search";
import Card from "@/src/components/card/Card";
import { Button, Image, Modal, Popover } from "antd";
import img1 from "@/asset/images/img1.png";
import { getListRooms } from "@/src/redux/slices/roomSlice";
import roomApi from "@/src/api/roomApi";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();
  const [openModal, setOpenModal] = useState(false);
  const dispatch = useDispatch();
  const [dataRoom, setDataRoom] = useState();
  const { listRooms } = useSelector((state) => state.rooms);
  useEffect(() => {
    dispatch(getListRooms());
  }, []);
  return (
    <div>
      <h1 className="text-5xl font-bold w-1/2 ">
        Find Your Perfect Rental Home
      </h1>
      <p className="my-5 ">
        An optimized platform to connect you with landlords and find your ideal
        rental space!
      </p>
      <ul>
        <li>🔹 Easily search by location</li>
        <li>🔹 Manage contracts & payments seamlessly</li>
        <li>🔹 Connect directly with landlords—no middleman</li>
      </ul>
      <Search />
      <h2 className="text-3xl font-bold">Popular</h2>
      <div className="grid my-8 sm:grid-cols-2 gap-8 md:grid-cols-3 lg:grid-cols-4">
        {listRooms &&
          listRooms.content.map((val, index) => (
            <Card
              onClick={() => {
                setOpenModal(true);
                setDataRoom(val);
              }}
              key={index}
              data={val}
            />
          ))}
      </div>
      {dataRoom && (
        <Modal
          open={openModal}
          footer={false}
          closeIcon={false}
          onCancel={() => setOpenModal(false)}
          width={800}
        >
          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-1 ">
              <Image
                src={dataRoom.image ? JSON.parse(dataRoom.image)[0] : img1}
                alt="Main room image"
                height={250}
                className="rounded-lg !w-full object-cover"
              />
            </div>
            <div className="flex flex-col justify-between w-full col-span-2">
              <div className="text-center flex justify-between">
                <h2 className="text-2xl font-bold">{dataRoom.roomNumber}</h2>
                <p className="text-blue-600 text-lg font-semibold mt-1">
                  {dataRoom.price}đ / tháng
                </p>
                <p
                  className={`text-sm font-medium mt-1 ${
                    dataRoom.status === "RENTED"
                      ? "text-red-500"
                      : "text-green-500"
                  }`}
                >
                  {dataRoom.status === "RENTED" ? "Rented" : "Available"}
                </p>
              </div>

              {/* Mô tả ngắn */}
              {dataRoom.description && (
                <p className="text-gray-600 text-sm leading-relaxed mb-2 px-2">
                  {dataRoom.description}
                </p>
              )}

              {/* Tiện ích */}
              {dataRoom.utilities && dataRoom.utilities.length > 0 && (
                <div className="flex flex-wrap justify-center gap-2 mb-2">
                  {dataRoom.utilities.map((utility, idx) => (
                    <span
                      key={idx}
                      className="px-3 py-1 bg-gray-100 rounded-full text-xs text-gray-700"
                    >
                      {utility}
                    </span>
                  ))}
                </div>
              )}

              {/* Danh sách ảnh nhỏ */}
              {dataRoom.image && JSON.parse(dataRoom.image).length > 1 && (
                <div className="flex flex-wrap gap-2 mb-2">
                  {JSON.parse(dataRoom.image)
                    .slice(1)
                    .map((val, idx) => (
                      <Image
                        key={idx}
                        src={val}
                        alt={`room-image-${idx}`}
                        width={80}
                        height={80}
                        className="rounded-lg object-cover"
                      />
                    ))}
                </div>
              )}
              <div className="flex justify-between gap-4">
                {/* Liên hệ chủ nhà */}
                <div className="text-center  mt-2">
                  <p className="text-sm text-gray-700 mb-1 font-semibold">
                    Liên hệ chủ nhà:
                  </p>
                  <a
                    href={`tel:0987654321`}
                    className="text-blue-500 underline text-base"
                  >
                    0987654321
                  </a>
                </div>

                <Button
                  disabled={dataRoom.status === "RENTED"}
                  onClick={() => router.push("https://zalo.me/0987654321")}
                  className="bg-blue-500 text-white rounded-lg px-4 py-2 mt-4 hover:bg-blue-600 transition duration-200 h-12 text-xl"
                >
                  Thuê ngay (Zalo)
                </Button>
              </div>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}

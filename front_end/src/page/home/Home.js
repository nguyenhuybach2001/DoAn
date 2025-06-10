"use client";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Image, Modal } from "antd";
import img1 from "@/asset/images/img1.png";
import { getListRooms } from "@/src/redux/slices/roomSlice";
import { useRouter } from "next/navigation";
import Card from "@/src/components/card/Card";

export default function Home() {
  const router = useRouter();
  const dispatch = useDispatch();

  // Modal
  const [openModal, setOpenModal] = useState(false);
  const [dataRoom, setDataRoom] = useState(null);

  // Search keyword
  const [searchKeyword, setSearchKeyword] = useState("");

  // Danh sách phòng từ redux
  const { listRooms } = useSelector((state) => state.rooms);

  useEffect(() => {
    dispatch(getListRooms());
  }, [dispatch]);

  // Filter danh sách phòng theo searchKeyword
  const filteredRooms =
    listRooms?.content?.filter((room) => {
      const keyword = searchKeyword.toLowerCase();
      // Bạn có thể thay đổi các trường lọc ở đây
      return (
        room.roomNumber?.toLowerCase().includes(keyword) ||
        room.description?.toLowerCase().includes(keyword) ||
        room.utilities?.some((u) => u.toLowerCase().includes(keyword))
      );
    }) || [];
  console.log(dataRoom, "khb");
  return (
    <div className="p-4 max-w-7xl mx-auto">
      <h1 className="text-5xl font-bold w-full md:w-1/2 mb-4">
        Find Your Perfect Rental Home
      </h1>
      <p className="my-5 max-w-xl">
        An optimized platform to connect you with landlords and find your ideal
        rental space!
      </p>
      <ul className="mb-5 list-disc list-inside max-w-xl">
        <li> Easily search by location</li>
        <li> Manage contracts & payments seamlessly</li>
        <li> Connect directly with landlords—no middleman</li>
      </ul>

      {/* Search Input */}
      <input
        type="text"
        placeholder="Tìm kiếm phòng..."
        className="mb-8 p-3 border border-gray-300 rounded-md w-full max-w-md"
        value={searchKeyword}
        onChange={(e) => setSearchKeyword(e.target.value)}
      />
      <div>
        <h2 className="text-3xl font-bold mb-4">Popular</h2>
      </div>
      <div className="grid my-8 sm:grid-cols-2 gap-8 md:grid-cols-3 lg:grid-cols-4">
        {filteredRooms.length > 0 ? (
          filteredRooms.map((val, index) => (
            <div
              key={index}
              onClick={() => {
                setOpenModal(true);
                setDataRoom(val);
              }}
              className="cursor-pointer"
            >
              <Card data={val} />
            </div>
          ))
        ) : (
          <p className="text-gray-500 col-span-full">
            Không tìm thấy phòng phù hợp.
          </p>
        )}
      </div>

      {/* Modal chi tiết phòng */}
      {dataRoom && (
        <Modal
          open={openModal}
          footer={false}
          closeIcon={false}
          onCancel={() => setOpenModal(false)}
          width={800}
          centered
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

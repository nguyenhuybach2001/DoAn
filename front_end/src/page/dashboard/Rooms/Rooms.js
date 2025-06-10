import Card from "@/src/components/card/Card";
import {
  Button,
  Cascader,
  Col,
  Drawer,
  Form,
  Image,
  Input,
  InputNumber,
  message,
  Card as CardAntd,
  Modal,
  Popconfirm,
  Row,
  Select,
  Table,
  Upload,
  DatePicker,
} from "antd";
import React, { useEffect, useMemo, useState } from "react";
import img1 from "@/asset/images/img1.png";
import buildingApi from "@/src/api/buildingApi";
import roomApi from "@/src/api/roomApi";
import { uploadImage } from "@/src/utils/uploadImage";
import { PlusOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { getListBuilding } from "@/src/redux/slices/buildingSlice";
import { getListRoomsByRole } from "@/src/redux/slices/roomSlice";
import { findLabelsFromValue } from "@/src/utils/filterAddress";
import statisticApi from "@/src/api/statisticApi";
import dayjs from "dayjs";
import { Bar, Line } from "react-chartjs-2";

const electricityPrice = 3000;
const waterPrice = 14000;

export default function Rooms() {
  const [openModal, setOpenModal] = useState({ open: false, mode: null });
  const [openFilter, setOpenFilter] = useState(false);
  const currentYear = dayjs().year();
  const [year, setYear] = useState(currentYear);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [openDrawer, setOpenDrawer] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [form] = Form.useForm();
  const [utilityUsagePerMonth, setUtilityUsagePerMonth] = useState({});
  const [dataRoom, setDataRoom] = useState();
  const dispatch = useDispatch();
  const { dataProvince } = useSelector((state) => state.province);
  const [fileList, setFileList] = useState([]);
  const { listBuilding } = useSelector((state) => state.building);
  const { listRoomsByRole } = useSelector((state) => state.rooms);
  useEffect(() => {
    const fetchData = async () => {
      const data = {
        year: year,
        room_id: dataRoom ? dataRoom.roomId : null,
      };
      const res = await statisticApi.getUtilityUsagePerMonth(data);
      if (res) {
        setUtilityUsagePerMonth(res);
      }
    };
    fetchData();
  }, [dataRoom, year]);
  const chartDataMoney = useMemo(() => {
    const labels = Object.keys(utilityUsagePerMonth).sort();
    const electricityCosts = labels.map(
      (m) => (utilityUsagePerMonth[m]?.electricity || 0) * electricityPrice
    );
    const waterCosts = labels.map(
      (m) => (utilityUsagePerMonth[m]?.water || 0) * waterPrice
    );

    return {
      labels: labels.map((m) => `Tháng ${m}`),
      datasets: [
        {
          label: "Tiền điện (VND)",
          data: electricityCosts,
          backgroundColor: "#3b82f6",
          borderRadius: 10,
        },
        {
          label: "Tiền nước (VND)",
          data: waterCosts,
          backgroundColor: "#10b981",
          borderRadius: 10,
        },
      ],
    };
  }, [utilityUsagePerMonth]);

  const chartOptionsMoney = {
    responsive: true,
    plugins: {
      title: {
        display: true,
        text: "Chi phí điện & nước theo tháng",
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: "VNĐ",
        },
      },
    },
  };
  const chartDataUsage = useMemo(() => {
    const labels = Object.keys(utilityUsagePerMonth).sort();
    const electricityUnits = labels.map(
      (m) => utilityUsagePerMonth[m]?.electricity || 0
    );
    const waterUnits = labels.map((m) => utilityUsagePerMonth[m]?.water || 0);

    return {
      labels: labels.map((m) => `Tháng ${m}`),
      datasets: [
        {
          label: "Số điện (kWh)",
          data: electricityUnits,
          borderColor: "#f97316",
          backgroundColor: "#f97316",
          tension: 0.4,
          fill: false,
        },
        {
          label: "Số nước (m³)",
          data: waterUnits,
          borderColor: "#0ea5e9",
          backgroundColor: "#0ea5e9",
          tension: 0.4,
          fill: false,
        },
      ],
    };
  }, [utilityUsagePerMonth]);
  const chartOptionsUsage = {
    responsive: true,
    plugins: {
      title: {
        display: true,
        text: "Mức tiêu thụ điện & nước theo tháng",
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: "Số lượng (kWh / m³)",
        },
      },
    },
  };

  const columns = [
    {
      title: "Số phòng",
      dataIndex: "roomNumber",
      sorter: (a, b) => a.roomNumber.localeCompare(b.roomNumber),
    },
    {
      title: "Loại phòng",
      dataIndex: "roomType",
      sorter: (a, b) => a.roomType.localeCompare(b.roomType),
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      filters: [
        { text: "Còn trống", value: "AVAILABLE" },
        { text: "Đã thuê", value: "RENTED" },
        { text: "Đã đặt", value: "RESERVED" },
        { text: "Đang bảo trì", value: "UNDER_MAINTEANCE" },
      ],
      onFilter: (value, record) => record.status === value,
    },
    {
      title: "Diện tích (m²)",
      dataIndex: "acreage",
      sorter: (a, b) => a.acreage - b.acreage,
    },
    {
      title: "Giá (VNĐ)",
      dataIndex: "price",
      sorter: (a, b) => a.price - b.price,
      render: (price) => `${price.toLocaleString()} đ`,
    },
    {
      title: "Tầng",
      dataIndex: "floor",
      sorter: (a, b) => a.floor - b.floor,
    },
    {
      title: "Số người tối đa",
      dataIndex: "maxOccupants",
      sorter: (a, b) => a.maxOccupants - b.maxOccupants,
    },
    {
      title: "Hoạt động",
      dataIndex: "isActive",
      render: (isActive) => (isActive ? "Có" : "Không"),
      filters: [
        { text: "Đang hoạt động", value: true },
        { text: "Ngưng hoạt động", value: false },
      ],
      onFilter: (value, record) => record.isActive === value,
    },
    {
      title: "Quản lý",
      dataIndex: "staff",
      render: (staff) => (staff && staff.email ? staff.email : <i>Chưa có</i>),
    },
  ];
  const handleYearChange = (date, dateString) => {
    setYear(dateString);
  };

  const onCreateRoom = async (values) => {
    const data = {
      roomNumber: values.roomNumber,
      acreage: values.acreage,
      price: values.price,
      status: values.status,
      roomType: values.roomType,
      floor: values.floor,
      maxOccupants: values.maxOccupants,
      image: JSON.stringify(values.imageRoom),
      description: values.description,
      buildingId: values.buildingId,
    };

    try {
      let res = null;

      if (openModal.mode === "edit") {
        res = await roomApi.updateRoom({ ...data, roomId: dataRoom.roomId });
      } else {
        res = await roomApi.createRoom(data);
      }
      if (res) {
        dispatch(getListRoomsByRole());
        dispatch(getListBuilding());
        form.resetFields();
        setFileList([]);
        setOpenModal({ open: false, mode: null });
      }
    } catch (error) {
      if (error.response?.status === 409) {
        form.setFields([
          {
            name: "roomNumber",
            errors: ["Số phòng này đã tồn tại trong toà nhà."],
          },
        ]);
      } else {
        message.error("Đã xảy ra lỗi khi tạo phòng!");
      }
    }
  };
  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }

    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };
  const handleRemove = async (file) => {
    const imagePath = file.url.replace("http://localhost:8080", "");
    try {
      await deleteImage(imagePath);
      setFileList((prev) => prev.filter((f) => f.uid !== file.uid));
    } catch (err) {
      message.error("Failed to delete image");
    }
  };
  const getBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
  };
  const showRoom = async (roomId) => {
    const data = Array.isArray(roomId) ? roomId : [roomId];
    const res = await roomApi.showRoom(data);
    if (res) {
      dispatch(getListRoomsByRole());
      setOpenDrawer(false);
    }
  };
  const handleEdit = () => {
    setOpenModal({ open: true, mode: "edit" });
    setOpenDrawer(false);

    const imageArray = JSON.parse(dataRoom.image) || [];

    const newFileList = imageArray.map((url, index) => ({
      uid: `${index}`,
      name: `image-${index}.png`,
      status: "done",
      url,
    }));
    setFileList(newFileList);

    // Gán giá trị vào form
    form.setFieldsValue({
      roomNumber: dataRoom.roomNumber,
      acreage: dataRoom.acreage,
      price: dataRoom.price,
      status: dataRoom.status,
      roomType: dataRoom.roomType,
      floor: dataRoom.floor,
      maxOccupants: dataRoom.maxOccupants,
      description: dataRoom.description,
      buildingId: dataRoom.buildingId,
      imageRoom: imageArray,
    });
  };
  const deleteRoom = async (roomId) => {
    const data = Array.isArray(roomId) ? roomId : [roomId];
    const res = await roomApi.deleteRoom(data);
    if (res) {
      dispatch(getListRoomsByRole());
      setOpenDrawer(false);
    }
  };
  return (
    <div>
      <div>
        <Button
          onClick={() => {
            setOpenModal({ open: true, mode: "create" });
          }}
        >
          Create room
        </Button>
      </div>
      <div className="my-8">
        <Table
          rowKey="roomId"
          columns={columns}
          dataSource={listRoomsByRole?.content || []}
          onRow={(record) => ({
            onClick: () => {
              setOpenDrawer(true);
              setDataRoom(record);
            },
          })}
          pagination={{ pageSize: 10 }}
        />
      </div>
      <Modal
        width={800}
        onCancel={() => {
          setOpenModal({ open: false, mode: null });
        }}
        title={openModal.mode == "create" ? "Create Room" : "Edit Room"}
        open={openModal.open}
        footer={false}
      >
        <Form form={form} onFinish={onCreateRoom} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Room Number"
                name="roomNumber"
                rules={[
                  { required: true, message: "Please input the room number!" },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Acreage"
                name="acreage"
                rules={[
                  { required: true, message: "Please input the acreage!" },
                ]}
              >
                <InputNumber />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Price"
                name="price"
                rules={[{ required: true, message: "Please input the price!" }]}
              >
                <InputNumber type="number" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Status"
                name="status"
                initialValue="AVAILABLE"
                rules={[
                  { required: true, message: "Please select the room status!" },
                ]}
              >
                <Select>
                  <Select.Option value="AVAILABLE">AVAILABLE</Select.Option>
                  <Select.Option value="RENTED">RENTED</Select.Option>
                  <Select.Option value="UNDER_MAINTEANCE">
                    UNDER_MAINTEANCE
                  </Select.Option>
                  <Select.Option value="RESERVED">RESERVED</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Room Type"
                name="roomType"
                initialValue="SINGLE"
                rules={[
                  { required: true, message: "Please select the room type!" },
                ]}
              >
                <Select>
                  <Select.Option value="SINGLE">SINGLE</Select.Option>
                  <Select.Option value="DOUBLE">DOUBLE</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Floor"
                name="floor"
                rules={[
                  { required: true, message: "Please select the floor!" },
                ]}
              >
                <Input type="number" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="Max Occupants"
                name="maxOccupants"
                rules={[
                  {
                    required: true,
                    message: "Please input the max number of occupants!",
                  },
                ]}
              >
                <Input type="number" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="Description"
                name="description"
                rules={[
                  { required: true, message: "Please input a description!" },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="Building"
            name="buildingId"
            rules={[{ required: true, message: "Please select a building!" }]}
          >
            <Select>
              {listBuilding &&
                listBuilding
                  .filter((item) => item.isActive)
                  .map((val, idx) => (
                    <Select.Option key={idx} value={val.id}>
                      {val.name}
                    </Select.Option>
                  ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="imageRoom"
            label="Upload Room Image"
            rules={[{ required: true, message: "Please upload an image!" }]}
          >
            <Upload
              listType="picture-card"
              fileList={fileList}
              customRequest={async ({ file, onSuccess, onError }) => {
                const res = await uploadImage(file);
                if ((res.status = "success")) {
                  const newFile = {
                    uid: file.uid,
                    name: file.name,
                    status: "done",
                    url: res.imageUrl,
                  };
                  const updatedFileList = [...fileList, newFile];
                  console.log(updatedFileList);
                  setFileList(updatedFileList);
                  form.setFieldsValue({
                    imageRoom: updatedFileList.map((item) => item.url),
                  });

                  onSuccess(res);
                } else {
                  onError(new Error(res.error));
                }
              }}
              onRemove={handleRemove}
              onPreview={handlePreview}
            >
              {fileList.length >= 8 ? null : (
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>Upload</div>
                </div>
              )}
            </Upload>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              {openModal.mode == "edit" ? "Edit Room" : "Create Room"}
            </Button>
          </Form.Item>
        </Form>
      </Modal>
      {previewImage && (
        <Image
          wrapperStyle={{ display: "none" }}
          preview={{
            visible: previewOpen,
            onVisibleChange: (visible) => setPreviewOpen(visible),
            afterOpenChange: (visible) => !visible && setPreviewImage(""),
          }}
          src={previewImage}
        />
      )}
      {dataRoom && (
        <Drawer
          width={720}
          title={dataRoom.roomNumber}
          open={openDrawer}
          onClose={() => setOpenDrawer(false)}
          extra={
            <div className="flex gap-4">
              {dataRoom.isActive ? (
                <>
                  <Button onClick={handleEdit}>Edit</Button>
                  <Popconfirm
                    title="Hidden the room"
                    description="Are you sure to hide this room?"
                    onConfirm={() => deleteRoom(dataRoom.roomId)}
                    okText="Yes"
                    cancelText="No"
                  >
                    <Button danger type="primary">
                      Hidden
                    </Button>
                  </Popconfirm>
                </>
              ) : (
                <Popconfirm
                  title="Show the room"
                  description="Are you sure to show this room?"
                  onConfirm={() => {
                    showRoom(dataRoom.roomId);
                  }}
                  okText="Yes"
                  cancelText="No"
                >
                  <Button className="bg-green-600 text-white">Show</Button>
                </Popconfirm>
              )}
            </div>
          }
          className="flex flex-col justify-between"
        >
          <div className="flex flex-col gap-4 flex-grow">
            {/* Hình ảnh */}
            <div>
              <p className="font-bold mb-2">Image:</p>
              <div className="flex gap-2 flex-wrap">
                {dataRoom.image
                  ? JSON.parse(dataRoom.image).map((val, idx) => (
                      <Image
                        className="!w-28 !h-28 rounded-lg object-cover"
                        key={idx}
                        alt="room-image"
                        src={val}
                      />
                    ))
                  : "No image"}
              </div>
            </div>

            {/* Địa chỉ */}
            <div>
              <p className="font-bold mb-2">Address:</p>
              <p>
                {findLabelsFromValue(
                  dataProvince,
                  JSON.parse(dataRoom.address)
                ).join(", ")}
              </p>
            </div>

            {/* Thông tin phòng */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="font-semibold">Room Number:</p>
                <p>{dataRoom.roomNumber}</p>
              </div>
              <div>
                <p className="font-semibold">Room Type:</p>
                <p>{dataRoom.roomType}</p>
              </div>
              <div>
                <p className="font-semibold">Status:</p>
                <p>{dataRoom.status}</p>
              </div>
              <div>
                <p className="font-semibold">Acreage (m²):</p>
                <p>{dataRoom.acreage}</p>
              </div>
              <div>
                <p className="font-semibold">Price (VNĐ):</p>
                <p>{dataRoom.price}đ</p>
              </div>
              <div>
                <p className="font-semibold">Floor:</p>
                <p>{dataRoom.floor}</p>
              </div>
              <div>
                <p className="font-semibold">Max Occupants:</p>
                <p>{dataRoom.maxOccupants}</p>
              </div>
              <div>
                <p className="font-semibold">Active:</p>
                <p>{dataRoom.isActive ? "Yes" : "No"}</p>
              </div>
              <div>
                <p className="font-semibold">Staff:</p>
                <p>{dataRoom?.staff?.email || "Chưa có"}</p>
              </div>
              <div>
                <p className="font-semibold">Description:</p>
                <p>{dataRoom.description}</p>
              </div>
            </div>

            {/* <CardAntd
              title="Thống kê tiền điện & nước"
              className="rounded-2xl shadow-md"
              extra={
                <DatePicker
                  onChange={handleYearChange}
                  defaultValue={dayjs()}
                  picker="year"
                />
              }
            >
              <Bar data={chartData} options={chartOptions} />
            </CardAntd> */}
            <CardAntd
              extra={
                <DatePicker
                  value={dayjs(year, "YYYY")}
                  onChange={handleYearChange}
                  defaultValue={dayjs()}
                  picker="year"
                />
              }
              title="Tiền điện & nước (VND)"
              className="mb-6 rounded-2xl shadow-md"
            >
              <Bar data={chartDataMoney} options={chartOptionsMoney} />
            </CardAntd>

            <CardAntd
              extra={
                <DatePicker
                  value={dayjs(year, "YYYY")}
                  onChange={handleYearChange}
                  defaultValue={dayjs()}
                  picker="year"
                />
              }
              title="Số lượng tiêu thụ điện & nước (kWh / m³)"
              className="rounded-2xl shadow-md"
            >
              <Line data={chartDataUsage} options={chartOptionsUsage} />
            </CardAntd>
          </div>
        </Drawer>
      )}
    </div>
  );
}

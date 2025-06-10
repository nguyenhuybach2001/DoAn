import React, { useEffect, useState } from "react";
import img1 from "@/asset/images/img1.png";
import {
  Button,
  Card,
  Cascader,
  Drawer,
  Form,
  Image as ImageAntd,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Table,
  Tag,
  Upload,
} from "antd";
import ImageNext from "next/image";
import { useDispatch, useSelector } from "react-redux";
import buildingApi from "@/src/api/buildingApi";
import { PlusOutlined } from "@ant-design/icons";
import { uploadImage } from "@/src/utils/uploadImage";
import { deleteImage } from "@/src/utils/deleteImage";
import { getListBuilding } from "@/src/redux/slices/buildingSlice";
import { findLabelsFromValue } from "@/src/utils/filterAddress";
import { getListRoomsByRole } from "@/src/redux/slices/roomSlice";

export default function Building() {
  const dispatch = useDispatch();
  const [openModal, setOpenModal] = useState({ open: false, mode: null });
  const [dataBuilding, setDataBuilding] = useState();
  const [fileList, setFileList] = useState([]);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const { dataProvince } = useSelector((state) => state.province);
  const [form] = Form.useForm();
  const { listBuilding } = useSelector((state) => state.building);
  useEffect(() => {
    !listBuilding && dispatch(getListBuilding());
  }, []);

  const onFinish = async (e) => {
    const data = {
      id: dataBuilding?.id,
      name: e.name,
      address: JSON.stringify(e.address),
      image: JSON.stringify(e.imageBuilding),
      isActive: dataBuilding?.isActive,
      electricityPrice: e.electricityPrice,
      waterPrice: e.waterPrice,
    };

    let res = null;

    if (openModal.mode === "edit") {
      res = await buildingApi.updateBuilding(data);
    } else {
      res = await buildingApi.createBuilding(
        data.name,
        data.address,
        data.image,
        data.waterPrice,
        data.electricityPrice
      );
    }
    if (res) {
      dispatch(getListBuilding());
      setOpenModal({ open: false, mode: null });
      form.resetFields();
      setFileList([]);
    }
  };
  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }

    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };

  const getBase64 = (file) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });
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

  const handleEdit = (building) => {
    setOpenModal({ open: true, mode: "edit" });
    setDataBuilding(building);

    const addressArray = JSON.parse(building.address);
    const imageArray = building.image ? JSON.parse(building.image) : [];

    const newFileList = imageArray.map((url, index) => ({
      uid: `${index}`,
      name: `image-${index}.png`,
      status: "done",
      url,
    }));
    setFileList(newFileList);

    form.setFieldsValue({
      name: building.name,
      address: addressArray,
      imageBuilding: imageArray,
      electricityPrice: building.electricityPrice,
      waterPrice: building.waterPrice,
      totalRoom: building.totalRoom,
    });
  };

  const handleHidden = async (buildingId) => {
    const res = await buildingApi.deleteBuilding(buildingId);
    if (res) {
      dispatch(getListBuilding());
      dispatch(getListRoomsByRole());
    }
  };

  const handleShow = async (buildingId) => {
    const res = await buildingApi.showBuilding(buildingId);
    if (res) {
      dispatch(getListBuilding());
      dispatch(getListRoomsByRole());
    }
  };

  return (
    <div>
      <Button
        onClick={() => {
          setOpenModal({ open: true, mode: "create" });
        }}
        className="mb-8"
      >
        Create building
      </Button>
      <Table
        columns={[
          {
            title: "Image",
            dataIndex: "image",
            key: "image",
            render: (image) => (
              <ImageAntd
                src={image}
                width={80}
                height={60}
                className="rounded-lg object-cover"
                alt="building"
              />
            ),
          },
          {
            title: "Building Name",
            dataIndex: "name",
            key: "name",
            sorter: (a, b) => a.name.localeCompare(b.name),
          },
          {
            title: "Address",
            dataIndex: "address",
            key: "address",
            sorter: (a, b) =>
              a.address.join(", ").localeCompare(b.address.join(", ")),
            render: (address) =>
              Array.isArray(address) ? address.join(", ") : address,
          },
          {
            title: "Total Rooms",
            dataIndex: "totalRoom",
            key: "totalRoom",
            sorter: (a, b) => a.totalRoom - b.totalRoom,
          },
          {
            title: "Giá điện (VND)",
            dataIndex: "electricityPrice",
            key: "electricityPrice",
            sorter: (a, b) => a.electricityPrice - b.electricityPrice,
            render: (price) => price?.toLocaleString("vi-VN") + " đ",
          },
          {
            title: "Giá nước (VND)",
            dataIndex: "waterPrice",
            key: "waterPrice",
            sorter: (a, b) => a.waterPrice - b.waterPrice,
            render: (price) => price?.toLocaleString("vi-VN") + " đ",
          },
          {
            title: "Status",
            dataIndex: "isActive",
            key: "isActive",
            sorter: (a, b) => a.isActive - b.isActive,
            render: (isActive) => (
              <Tag color={isActive ? "green" : "red"}>
                {isActive ? "Active" : "Inactive"}
              </Tag>
            ),
          },
          {
            title: "Actions",
            key: "actions",
            render: (text, record) => (
              <div className="flex gap-2">
                <Button size="small" onClick={() => handleEdit(record.rawData)}>
                  Edit
                </Button>
                {record.isActive ? (
                  <Popconfirm
                    title="Hide this building?"
                    onConfirm={() => handleHidden(record.rawData.id)}
                    okText="Yes"
                    cancelText="No"
                  >
                    <Button size="small" danger>
                      Hide
                    </Button>
                  </Popconfirm>
                ) : (
                  <Popconfirm
                    title="Show this building?"
                    onConfirm={() => handleShow(record.rawData.id)}
                    okText="Yes"
                    cancelText="No"
                  >
                    <Button size="small" type="primary">
                      Show
                    </Button>
                  </Popconfirm>
                )}
              </div>
            ),
          },
        ]}
        dataSource={
          listBuilding?.map((val, index) => ({
            key: val.id || index,
            image: JSON.parse(val.image)?.[0] || img1,
            name: val.name,
            address: findLabelsFromValue(dataProvince, JSON.parse(val.address)),
            totalRoom: val.totalRoom,
            isActive: val.isActive,
            electricityPrice: val.electricityPrice,
            waterPrice: val.waterPrice,
            rawData: val,
          })) || []
        }
        pagination={{ pageSize: 8 }}
        rowClassName="hover:!bg-gray-100 transition-all"
      />

      <Modal
        open={openModal.open}
        onCancel={() => {
          setOpenModal({ open: false, mode: null });
        }}
        footer={false}
        closeIcon={false}
        title={
          <h2 className="text-xl font-semibold">
            {openModal.mode === "create" ? "Tạo toà nhà" : "Chỉnh sửa toà nhà"}
          </h2>
        }
      >
        <Form
          form={form}
          onFinish={onFinish}
          layout="vertical"
          className="space-y-4"
        >
          <Form.Item
            label={<span className="font-medium">Tên toà nhà</span>}
            name="name"
            rules={[{ required: true, message: "Vui lòng nhập tên!" }]}
          >
            <Input
              className="rounded-md p-2 shadow-sm"
              placeholder="VD: Toà nhà A"
            />
          </Form.Item>

          <Form.Item
            label={<span className="font-medium">Địa chỉ</span>}
            name="address"
            rules={[{ required: true, message: "Vui lòng chọn địa chỉ!" }]}
          >
            <Cascader
              options={dataProvince}
              className="w-full"
              placeholder="Chọn địa chỉ"
            />
          </Form.Item>

          <div className="grid grid-cols-2 gap-4">
            <Form.Item
              label={<span className="font-medium">Giá 1 số điện (VND)</span>}
              name="electricityPrice"
              rules={[{ required: true, message: "Vui lòng nhập giá điện!" }]}
            >
              <InputNumber
                className="w-full rounded-md shadow-sm"
                placeholder="VD: 3000"
              />
            </Form.Item>

            <Form.Item
              label={<span className="font-medium">Giá 1 khối nước (VND)</span>}
              name="waterPrice"
              rules={[{ required: true, message: "Vui lòng nhập giá nước!" }]}
            >
              <InputNumber
                className="w-full rounded-md shadow-sm"
                placeholder="VD: 14000"
              />
            </Form.Item>
          </div>

          <Form.Item
            name="imageBuilding"
            label="Upload"
            rules={[
              {
                required: true,
                message: "Please input your image!",
              },
            ]}
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
                    url: res.imageUrl, // ảnh preview
                  };
                  const updatedFileList = [...fileList, newFile];
                  setFileList(updatedFileList);
                  form.setFieldsValue({
                    imageBuilding: updatedFileList.map((item) => item.url),
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
            <Button
              type="primary"
              htmlType="submit"
              className="w-full mt-2 rounded-md"
            >
              {openModal.mode === "create" ? "Tạo toà nhà" : "Cập nhật"}
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      {previewImage && (
        <ImageAntd
          wrapperStyle={{ display: "none" }}
          preview={{
            visible: previewOpen,
            onVisibleChange: (visible) => setPreviewOpen(visible),
            afterOpenChange: (visible) => !visible && setPreviewImage(""),
          }}
          src={previewImage}
        />
      )}
    </div>
  );
}

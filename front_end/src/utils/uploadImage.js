export const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await fetch("http://localhost:8080/api/upload/image", {
      method: "POST",
      body: formData,
    });

    const data = await res.json();

    if (data.status === "success") {
      return {
        success: true,
        imageUrl: "http://localhost:8080" + data.imageUrl,
      };
    } else {
      return { success: false, error: "Upload failed" };
    }
  } catch (err) {
    return { success: false, error: err.message };
  }
};

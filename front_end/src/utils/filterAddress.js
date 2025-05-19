export const findLabelsFromValue = (options, valueArr) => {
    let labels = [];
    let currentOptions = options;

    for (let i = 0; i < valueArr.length; i++) {
      const value = valueArr[i];

      const match = currentOptions.find((opt) => opt.value === value);
      if (!match) {
        break;
      }

      labels.push(match.label);
      currentOptions = match.children || [];
    }

    return labels;
  };
export default function responseToDataOptions(response: any) {
    return Object.entries(response.data).map(([name, value]) => ({ name, value }));
}
  
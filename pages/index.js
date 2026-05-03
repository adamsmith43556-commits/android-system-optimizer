export default function Home() {
  return (
    <div style={{ textAlign: 'center', marginTop: '100px', fontFamily: 'sans-serif', color: '#444' }}>
      <h1 style={{ fontSize: '2.5rem' }}>Node Status: <span style={{ color: '#00b875' }}>Active</span></h1>
      <p style={{ fontSize: '1.2rem' }}>CDN Optimization and Secure Syncing in progress.</p>
      <hr style={{ width: '50%', margin: '20px auto', border: '0.5px solid #eee' }} />
      <code style={{ background: '#f4f4f4', padding: '5px 10px', borderRadius: '5px' }}>
        v2.6.0-stable-build
      </code>
    </div>
  );
}


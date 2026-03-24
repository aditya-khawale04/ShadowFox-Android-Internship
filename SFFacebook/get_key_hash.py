import base64
import hashlib
import subprocess
import sys

def get_facebook_keyhash():
    """Generate Facebook key hash from Android debug keystore"""
    try:
        # Export certificate
        cmd = [
            'keytool',
            '-exportcert',
            '-alias', 'androiddebugkey',
            '-keystore', f'{sys.path[0]}\\..\\..\\..\\..\\..\\..\\Users\\{sys.platform}',
        ]
        
        # Try simpler approach using direct keytool
        import os
        keystore_path = os.path.expanduser('~\\.android\\debug.keystore')
        
        result = subprocess.run([
            'keytool',
            '-exportcert',
            '-alias', 'androiddebugkey',
            '-keystore', keystore_path,
            '-storepass', 'android',
            '-keypass', 'android'
        ], capture_output=True)
        
        if result.returncode == 0:
            # Get certificate bytes
            cert = result.stdout
            
            # Calculate SHA1 hash
            sha_hash = hashlib.sha1(cert).digest()
            
            # Encode to base64
            key_hash = base64.b64encode(sha_hash).decode('utf-8')
            
            print("Facebook Key Hash:")
            print(key_hash)
            return key_hash
        else:
            print("Error:", result.stderr.decode())
            
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    get_facebook_keyhash()

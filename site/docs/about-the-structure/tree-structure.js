import { useState } from 'react';
import { FaFolder, FaFolderOpen, FaFile } from 'react-icons/fa';

const TreeNode = ({ node }) => {
  const [isOpen, setIsOpen] = useState(false);

  const toggleOpen = () => setIsOpen(!isOpen);

  const color = '#6a0dad'

  return (
    <div className="pl-4">
      <div
        className="flex items-center cursor-pointer"
        onClick={node.type === 'folder' ? toggleOpen : undefined}
      >
        {node.type === 'folder' ? (
          isOpen ? <FaFolderOpen className="mr-2" size={50} style={{ color: color }}/> : <FaFolder className="mr-2" size={50} style={{ color: color }}/>
        ) : (
          <FaFile className="mr-2" size={40} style={{ color: color }}/>
        )}
        <span style={{ fontSize: '18px' }}>{node.name}</span> 
      </div>
      {isOpen && node.children && (
        <div style={{ marginLeft: '50px' }}>
          {node.children.map((child) => (
            <TreeNode key={child.name} node={child}/>
          ))}
        </div>
      )}
    </div>
  );
};

export default TreeNode;
